package com.car.business.logic.service;

import com.car.business.domain.Cliente;
import com.car.business.dto.ClienteDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.ClienteMapper;
import com.car.business.percistence.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ClienteService extends BaseService<Cliente, ClienteDto, String> {

    public ClienteService(ClienteRepository repository, ClienteMapper mapper) {
        super(repository, mapper);
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
        if (entidad.getContacto() == null) {
            throw new BusinessException("El contacto es obligatorio.");
        }
        if (entidad.getDireccion() == null) {
            throw new BusinessException("La dirección es obligatoria.");
        }
        if (entidad.getImagen() == null) {
            throw new BusinessException("La imagen es obligatoria.");
        }
    }
}
