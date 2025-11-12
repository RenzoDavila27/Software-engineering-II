package com.car.business.logic.service;

import com.car.business.domain.Pais;
import com.car.business.dto.PaisDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.PaisMapper;
import com.car.business.percistence.repository.PaisRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class PaisService extends BaseService<Pais, PaisDto, String> {

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository repository, PaisMapper mapper) {
        super(repository, mapper);
        this.paisRepository = repository;
    }

    @Override
    protected void validar(Pais entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El país es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre del país es obligatorio.");
        }
    }

    public Optional<Pais> findByNombre(String nombre) {
        return paisRepository.findByNombre(nombre);
    }
}
