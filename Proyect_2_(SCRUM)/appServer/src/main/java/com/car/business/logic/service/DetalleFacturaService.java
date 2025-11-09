package com.car.business.logic.service;

import com.car.business.domain.DetalleFactura;
import com.car.business.dto.DetalleFacturaDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.DetalleFacturaMapper;
import com.car.business.percistence.repository.DetalleFacturaRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleFacturaService extends BaseService<DetalleFactura, DetalleFacturaDto, String> {

    public DetalleFacturaService(DetalleFacturaRepository repository, DetalleFacturaMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(DetalleFactura entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El detalle de factura es obligatorio.");
        }
        if (entidad.getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a cero.");
        }
        if (entidad.getSubtotal() < 0) {
            throw new BusinessException("El subtotal no puede ser negativo.");
        }
        if (entidad.getAlquiler() == null) {
            throw new BusinessException("El alquiler asociado es obligatorio.");
        }
    }
}
