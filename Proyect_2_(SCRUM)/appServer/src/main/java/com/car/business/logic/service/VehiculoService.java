package com.car.business.logic.service;

import com.car.business.domain.Vehiculo;
import com.car.business.dto.VehiculoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.VehiculoMapper;
import com.car.business.percistence.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VehiculoService extends BaseService<Vehiculo, VehiculoDto, String> {

    public VehiculoService(VehiculoRepository repository, VehiculoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Vehiculo entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El vehículo es obligatorio.");
        }
        if (entidad.getEstadoVehiculo() == null) {
            throw new BusinessException("El estado del vehículo es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getPatente())) {
            throw new BusinessException("La patente es obligatoria.");
        }
        if (entidad.getCaracteristicaVehiculo() == null) {
            throw new BusinessException("La característica del vehículo es obligatoria.");
        }
    }
}
