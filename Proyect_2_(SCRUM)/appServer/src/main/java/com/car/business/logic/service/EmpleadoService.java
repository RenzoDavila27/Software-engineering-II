package com.car.business.logic.service;

import com.car.business.domain.Empleado;
import com.car.business.dto.EmpleadoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.EmpleadoMapper;
import com.car.business.percistence.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmpleadoService extends BaseService<Empleado, EmpleadoDto, String> {

    public EmpleadoService(EmpleadoRepository repository, EmpleadoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Empleado entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El empleado es obligatorio.");
        }
        validarDatosPersona(entidad);
        if (entidad.getTipoEmpleado() == null) {
            throw new BusinessException("El tipo de empleado es obligatorio.");
        }
    }

    private void validarDatosPersona(Empleado entidad) throws BusinessException {
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
