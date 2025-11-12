package com.car.business.logic.service;

import com.car.business.domain.Departamento;
import com.car.business.domain.Localidad;
import com.car.business.dto.LocalidadDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.LocalidadMapper;
import com.car.business.percistence.repository.LocalidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class LocalidadService extends BaseService<Localidad, LocalidadDto, String> {

    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository repository, LocalidadMapper mapper) {
        super(repository, mapper);
        this.localidadRepository = repository;
    }

    @Override
    protected void validar(Localidad entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La localidad es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre de la localidad es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getCodigoPostal())) {
            throw new BusinessException("El código postal es obligatorio.");
        }
        if (entidad.getDepartamento() == null) {
            throw new BusinessException("El departamento asociado es obligatorio.");
        }
    }

    public Optional<Localidad> findByNombreAndCodigoPostalAndDepartamento(String nombre, String codigoPostal, Departamento departamento) {
        return localidadRepository.findByNombreAndCodigoPostalAndDepartamento(nombre, codigoPostal, departamento);
    }
}
