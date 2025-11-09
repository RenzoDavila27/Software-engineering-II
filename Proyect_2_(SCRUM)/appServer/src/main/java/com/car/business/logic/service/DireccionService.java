package com.car.business.logic.service;

import com.car.business.domain.Direccion;
import com.car.business.dto.DireccionDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.DireccionMapper;
import com.car.business.percistence.repository.DireccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DireccionService extends BaseService<Direccion, DireccionDto, String> {

    public DireccionService(DireccionRepository repository, DireccionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Direccion entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La dirección es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getCalle())) {
            throw new BusinessException("La calle es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNumeracion())) {
            throw new BusinessException("La numeración es obligatoria.");
        }
        if (entidad.getLocalidad() == null) {
            throw new BusinessException("La localidad es obligatoria.");
        }
    }
}
