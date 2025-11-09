package com.car.business.logic.service;

import com.car.business.domain.CostoVehiculo;
import com.car.business.dto.CostoVehiculoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.CostoVehiculoMapper;
import com.car.business.percistence.repository.CostoVehiculoRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class CostoVehiculoService extends BaseService<CostoVehiculo, CostoVehiculoDto, String> {

    public CostoVehiculoService(CostoVehiculoRepository repository, CostoVehiculoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(CostoVehiculo entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El costo del vehículo es obligatorio.");
        }
        LocalDate desde = entidad.getFechaDesde();
        if (desde == null) {
            throw new BusinessException("La fecha desde es obligatoria.");
        }
        LocalDate hasta = entidad.getFechaHasta();
        if (hasta == null) {
            throw new BusinessException("La fecha hasta es obligatoria.");
        }
        if (hasta.isBefore(desde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
        if (entidad.getCosto() == null || entidad.getCosto() <= 0) {
            throw new BusinessException("El costo debe ser mayor a cero.");
        }
    }
}
