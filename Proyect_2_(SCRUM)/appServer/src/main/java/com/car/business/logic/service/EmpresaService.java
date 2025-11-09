package com.car.business.logic.service;

import com.car.business.domain.Empresa;
import com.car.business.dto.EmpresaDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.EmpresaMapper;
import com.car.business.percistence.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmpresaService extends BaseService<Empresa, EmpresaDto, String> {

    public EmpresaService(EmpresaRepository repository, EmpresaMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Empresa entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La empresa es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre de la empresa es obligatorio.");
        }
    }
}
