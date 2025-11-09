package com.car.business.logic.service;

import com.car.business.domain.Factura;
import com.car.business.dto.FacturaDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.FacturaMapper;
import com.car.business.percistence.repository.FacturaRepository;
import org.springframework.stereotype.Service;

@Service
public class FacturaService extends BaseService<Factura, FacturaDto, String> {

    public FacturaService(FacturaRepository repository, FacturaMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Factura entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La factura es obligatoria.");
        }
        if (entidad.getNumeroFactura() == null) {
            throw new BusinessException("El número de factura es obligatorio.");
        }
        if (entidad.getFechaFactura() == null) {
            throw new BusinessException("La fecha de la factura es obligatoria.");
        }
        if (entidad.getEstado() == null) {
            throw new BusinessException("El estado de la factura es obligatorio.");
        }
        if (entidad.getTotalPagado() != null && entidad.getTotalPagado() < 0) {
            throw new BusinessException("El total pagado no puede ser negativo.");
        }
        if (entidad.getDetalles() == null || entidad.getDetalles().isEmpty()) {
            throw new BusinessException("La factura debe tener al menos un detalle.");
        }
    }
}
