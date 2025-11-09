package com.car.business.logic.service;

import com.car.business.domain.FormaDePago;
import com.car.business.dto.FormaDePagoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.FormaDePagoMapper;
import com.car.business.percistence.repository.FormaDePagoRepository;
import org.springframework.stereotype.Service;

@Service
public class FormaDePagoService extends BaseService<FormaDePago, FormaDePagoDto, String> {

    public FormaDePagoService(FormaDePagoRepository repository, FormaDePagoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(FormaDePago entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La forma de pago es obligatoria.");
        }
        if (entidad.getTipoPago() == null) {
            throw new BusinessException("El tipo de pago es obligatorio.");
        }
    }
}
