package com.car.clientead.business.logic;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.EmpresaDto;
import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.ContactoCorreoElectronicoRepository;
import com.car.clientead.repository.EmpresaRepository;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ContactoCorreoElectronicoRepository contactoCorreoRepository;

    public List<EmpresaDto> listar() {
        return empresaRepository.findAll().stream()
                .filter(this::esEmpresaValida)
                .collect(Collectors.toList());
    }

    public EmpresaDto consultar(String id) {
        return empresaRepository.findById(id);
    }

    public EmpresaDto crear(EmpresaDto dto, ContactoCorreoElectronicoDto contactoCorreo) {
        validarEmpresa(dto);
        boolean existiaContacto = StringUtils.hasText(dto.getContactoId());
        String contactoId = guardarContacto(dto.getContactoId(), contactoCorreo);
        dto.setContactoId(contactoId);
        try {
            return empresaRepository.create(dto);
        } catch (RuntimeException ex) {
            if (!existiaContacto) {
                eliminarContactoSilencioso(contactoId);
            }
            throw ex;
        }
    }

    public EmpresaDto modificar(String id, EmpresaDto dto, ContactoCorreoElectronicoDto contactoCorreo) {
        validarEmpresa(dto);
        String actualContactoId = dto.getContactoId();
        boolean existiaContacto = StringUtils.hasText(actualContactoId);
        String contactoId = guardarContacto(actualContactoId, contactoCorreo);
        dto.setContactoId(contactoId);
        try {
            return empresaRepository.update(id, dto);
        } catch (RuntimeException ex) {
            if (!existiaContacto) {
                eliminarContactoSilencioso(contactoId);
            }
            throw ex;
        }
    }

    public void eliminar(String id) {
        EmpresaDto existente = null;
        try {
            existente = empresaRepository.findById(id);
        } catch (ApiClientException ignored) {
        }
        empresaRepository.delete(id);
        if (existente != null) {
            eliminarContactoSilencioso(existente.getContactoId());
        }
    }

    public ContactoCorreoElectronicoDto obtenerContacto(String contactoId) {
        if (!StringUtils.hasText(contactoId)) {
            return null;
        }
        return contactoCorreoRepository.findById(contactoId);
    }

    public Map<String, ContactoCorreoElectronicoDto> mapearContactos(List<EmpresaDto> empresas) {
        if (CollectionUtils.isEmpty(empresas)) {
            return Collections.emptyMap();
        }
        Set<String> ids = empresas.stream()
                .filter(Objects::nonNull)
                .map(EmpresaDto::getContactoId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return ids.stream()
                .map(this::obtenerContactoSilencioso)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ContactoCorreoElectronicoDto::getId, dto -> dto, (a, b) -> a));
    }

    private String guardarContacto(String contactoId, ContactoCorreoElectronicoDto contacto) {
        ContactoCorreoElectronicoDto datos = prepararContacto(contacto);
        if (StringUtils.hasText(contactoId)) {
            contactoCorreoRepository.update(contactoId, datos);
            return contactoId;
        }
        ContactoCorreoElectronicoDto creado = contactoCorreoRepository.create(datos);
        if (creado == null || !StringUtils.hasText(creado.getId())) {
            throw new ApiClientException("No se pudo registrar el contacto de correo de la empresa.");
        }
        return creado.getId();
    }

    private ContactoCorreoElectronicoDto prepararContacto(ContactoCorreoElectronicoDto contacto) {
        if (contacto == null) {
            throw new IllegalArgumentException("Debe completar el correo electrónico de contacto.");
        }
        if (!StringUtils.hasText(contacto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        ContactoCorreoElectronicoDto datos = new ContactoCorreoElectronicoDto();
        datos.setEmail(contacto.getEmail());
        datos.setObservacion(contacto.getObservacion());
        datos.setTipoContacto(contacto.getTipoContacto() != null ? contacto.getTipoContacto() : TipoContacto.EMPRESA);
        return datos;
    }

    private void eliminarContactoSilencioso(String contactoId) {
        if (!StringUtils.hasText(contactoId)) {
            return;
        }
        try {
            contactoCorreoRepository.delete(contactoId);
        } catch (RuntimeException ignored) {
        }
    }

    private ContactoCorreoElectronicoDto obtenerContactoSilencioso(String contactoId) {
        if (!StringUtils.hasText(contactoId)) {
            return null;
        }
        try {
            return contactoCorreoRepository.findById(contactoId);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private void validarEmpresa(EmpresaDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la empresa son obligatorios.");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new IllegalArgumentException("Debe indicar el nombre de la empresa.");
        }
        if (!StringUtils.hasText(dto.getPersonaId())) {
            throw new IllegalArgumentException("Debe seleccionar la persona responsable.");
        }
    }

    private boolean esEmpresaValida(EmpresaDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }
}
