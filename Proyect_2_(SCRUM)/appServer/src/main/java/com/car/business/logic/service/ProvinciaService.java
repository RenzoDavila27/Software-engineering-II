package com.car.business.logic.service;

import com.car.business.domain.Pais;
import com.car.business.domain.Provincia;
import com.car.business.dto.ProvinciaDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.ProvinciaMapper;
import com.car.business.percistence.repository.ProvinciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class ProvinciaService extends BaseService<Provincia, ProvinciaDto, String> {

    private final ProvinciaRepository provinciaRepository;

    public ProvinciaService(ProvinciaRepository repository, ProvinciaMapper mapper) {
        super(repository, mapper);
        this.provinciaRepository = repository;
    }

    @Override
    protected void validar(Provincia entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La provincia es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre de la provincia es obligatorio.");
        }
        if (entidad.getPais() == null) {
            throw new BusinessException("El país asociado es obligatorio.");
        }
    }

    public Optional<Provincia> findByNombreAndPais(String nombre, Pais pais) {
        return provinciaRepository.findByNombreAndPais(nombre, pais);
    }
}
