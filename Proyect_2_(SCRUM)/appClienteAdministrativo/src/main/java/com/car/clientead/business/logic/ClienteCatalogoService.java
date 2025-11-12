package com.car.clientead.business.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.business.logic.view.SelectOptionView;
import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.repository.ContactoCorreoElectronicoRepository;
import com.car.clientead.repository.ContactoTelefonicoRepository;
import com.car.clientead.repository.DireccionRepository;
import com.car.clientead.repository.ImagenRepository;

@Service
public class ClienteCatalogoService {

    @Autowired
    private ContactoTelefonicoRepository contactoTelefonicoRepository;

    @Autowired
    private ContactoCorreoElectronicoRepository contactoCorreoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    public List<SelectOptionView> listarContactos() {
        List<SelectOptionView> opciones = new ArrayList<>();

        List<ContactoTelefonicoDto> telefonos = contactoTelefonicoRepository.findAll();
        opciones.addAll(telefonos.stream()
                .filter(dto -> dto != null && StringUtils.hasText(dto.getId()))
                .map(dto -> new SelectOptionView(
                        dto.getId(),
                        construirDescripcionTelefono(dto)))
                .collect(Collectors.toList()));

        List<ContactoCorreoElectronicoDto> correos = contactoCorreoRepository.findAll();
        opciones.addAll(correos.stream()
                .filter(dto -> dto != null && StringUtils.hasText(dto.getId()))
                .map(dto -> new SelectOptionView(
                        dto.getId(),
                        construirDescripcionCorreo(dto)))
                .collect(Collectors.toList()));

        return opciones;
    }

    public List<SelectOptionView> listarDirecciones() {
        List<DireccionDto> direcciones = direccionRepository.findAll();
        return direcciones.stream()
                .filter(dto -> dto != null && StringUtils.hasText(dto.getId()))
                .map(dto -> new SelectOptionView(dto.getId(), construirDescripcionDireccion(dto)))
                .collect(Collectors.toList());
    }

    public List<SelectOptionView> listarImagenes() {
        List<ImagenDto> imagenes = imagenRepository.findAll();
        return imagenes.stream()
                .filter(dto -> dto != null && StringUtils.hasText(dto.getId()))
                .map(dto -> new SelectOptionView(dto.getId(), construirDescripcionImagen(dto)))
                .collect(Collectors.toList());
    }

    private String construirDescripcionTelefono(ContactoTelefonicoDto dto) {
        StringBuilder sb = new StringBuilder("Tel: ");
        if (StringUtils.hasText(dto.getTelefono())) {
            sb.append(dto.getTelefono());
        } else {
            sb.append("Sin número");
        }
        if (StringUtils.hasText(dto.getTipoTelefono())) {
            sb.append(" (").append(dto.getTipoTelefono()).append(")");
        }
        return sb.toString();
    }

    private String construirDescripcionCorreo(ContactoCorreoElectronicoDto dto) {
        StringBuilder sb = new StringBuilder("Email: ");
        if (StringUtils.hasText(dto.getEmail())) {
            sb.append(dto.getEmail());
        } else {
            sb.append("Sin dirección");
        }
        if (StringUtils.hasText(dto.getObservacion())) {
            sb.append(" (").append(dto.getObservacion()).append(")");
        }
        return sb.toString();
    }

    private String construirDescripcionDireccion(DireccionDto dto) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(dto.getCalle())) {
            sb.append(dto.getCalle());
        }
        if (StringUtils.hasText(dto.getNumeracion())) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(dto.getNumeracion());
        }
        if (StringUtils.hasText(dto.getBarrio())) {
            sb.append(" - Barrio ").append(dto.getBarrio());
        }
        if (StringUtils.hasText(dto.getManzanaPiso())) {
            sb.append(" Mza/Piso ").append(dto.getManzanaPiso());
        }
        if (StringUtils.hasText(dto.getCasaDepartamento())) {
            sb.append(" Dept ").append(dto.getCasaDepartamento());
        }
        return sb.length() > 0 ? sb.toString() : "Dirección sin detalles";
    }

    private String construirDescripcionImagen(ImagenDto dto) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(dto.getNombre())) {
            sb.append(dto.getNombre());
        } else {
            sb.append("Imagen sin nombre");
        }
        if (dto.getTipoImagen() != null) {
            sb.append(" (").append(dto.getTipoImagen()).append(")");
        }
        return sb.toString();
    }

}
