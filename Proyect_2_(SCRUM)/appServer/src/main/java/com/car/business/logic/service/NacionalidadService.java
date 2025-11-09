package com.car.business.logic.service;

import com.car.business.domain.Nacionalidad;
import com.car.business.dto.NacionalidadDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.NacionalidadMapper;
import com.car.business.percistence.repository.NacionalidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NacionalidadService extends BaseService<Nacionalidad, NacionalidadDto, String> {

    public NacionalidadService(NacionalidadRepository repository, NacionalidadMapper mapper) {
        super(repository, mapper);
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
}
