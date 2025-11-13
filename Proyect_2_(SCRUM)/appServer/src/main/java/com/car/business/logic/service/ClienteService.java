package com.car.business.logic.service;

import com.car.business.domain.Cliente;
import com.car.business.domain.Contacto;
import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.domain.ContactoTelefonico;
import com.car.business.dto.ClienteDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.ClienteMapper;
import com.car.business.percistence.repository.ClienteRepository;
import com.car.business.percistence.repository.ContactoCorreoElectronicoRepository;
import com.car.business.percistence.repository.ContactoTelefonicoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ClienteService extends BaseService<Cliente, ClienteDto, String> {

    public ClienteService(ClienteRepository repository, ClienteMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private ContactoTelefonicoRepository contactoTelefonicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContactoCorreoElectronicoRepository contactoCorreoElectronicoRepository;

    @Override
    protected void preAlta(Cliente entidad) throws BusinessException {
        prepararContactos(entidad);
    }

    @Override
    protected void preModificacion(Cliente entidad) throws BusinessException {
        prepararContactos(entidad);
    }

    @Override
    protected void validar(Cliente entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El cliente es obligatorio.");
        }
        validarDatosPersona(entidad);
        if (!StringUtils.hasText(entidad.getDireccionEstadia())) {
            throw new BusinessException("La dirección de estadía es obligatoria.");
        }
        if (entidad.getNacionalidad() == null) {
            throw new BusinessException("La nacionalidad es obligatoria.");
        }
    }

    private void validarDatosPersona(Cliente entidad) throws BusinessException {
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getApellido())) {
            throw new BusinessException("El apellido es obligatorio.");
        }
        if (entidad.getFechaNacimiento() == null) {
            throw new BusinessException("La fecha de nacimiento es obligatoria.");
        }
        if (entidad.getTipoDocumento() == null) {
            throw new BusinessException("El tipo de documento es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNumeroDocumento())) {
            throw new BusinessException("El número de documento es obligatorio.");
        }
        if (entidad.getContactos() == null || entidad.getContactos().isEmpty()) {
            log.warn("Validación Cliente: sin contactos asociados para {}", entidad.getNumeroDocumento());
            throw new BusinessException("El contacto es obligatorio.");
        }
        if (entidad.getDireccion() == null) {
            log.warn("Validación Cliente: sin dirección asociada para {}", entidad.getNumeroDocumento());
            throw new BusinessException("La dirección es obligatoria.");
        }
        if (entidad.getImagen() == null) {
            log.warn("Validación Cliente: sin imagen asociada para {}", entidad.getNumeroDocumento());
            throw new BusinessException("La imagen es obligatoria.");
        }
    }

    private void prepararContactos(Cliente entidad) throws BusinessException {
        if (entidad.getContactos() == null || entidad.getContactos().isEmpty()) {
            return;
        }
        Contacto seleccionado = entidad.getContactos().get(0);
        if (seleccionado == null || !StringUtils.hasText(seleccionado.getId())) {
            return;
        }
        Contacto existente = obtenerContactoExistente(seleccionado.getId())
                .orElseThrow(() -> new BusinessException("No se encontró el contacto seleccionado."));
        existente.setPersona(entidad);
        List<Contacto> contactos = new ArrayList<>();
        contactos.add(existente);
        entidad.setContactos(contactos);
    }

    private Optional<Contacto> obtenerContactoExistente(String id) {
        Optional<ContactoTelefonico> tel = contactoTelefonicoRepository.findById(id);
        if (tel.isPresent()) {
            return tel.map(t -> (Contacto) t);
        }
        return contactoCorreoElectronicoRepository.findById(id)
                .map(c -> (Contacto) c);
    }

    public Optional<Cliente> findByUserId(String id) {

        return clienteRepository.findByUserId(id);

    }
}
