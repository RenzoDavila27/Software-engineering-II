package com.car.business.logic.service;

import com.car.business.domain.Nacionalidad;
import com.car.business.dto.NacionalidadDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.NacionalidadMapper;
import com.car.business.percistence.repository.NacionalidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class NacionalidadService extends BaseService<Nacionalidad, NacionalidadDto, String> {

    private final NacionalidadRepository nacionalidadRepository;

    public NacionalidadService(NacionalidadRepository repository, NacionalidadMapper mapper) {
        super(repository, mapper);
        this.nacionalidadRepository = repository;
    }

    @Override
    protected void validar(Nacionalidad entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La nacionalidad es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre de la nacionalidad es obligatorio.");
        }
    }

    public Optional<Nacionalidad> findByNombre(String nombre) {
        return nacionalidadRepository.findByNombre(nombre);
    }
}
