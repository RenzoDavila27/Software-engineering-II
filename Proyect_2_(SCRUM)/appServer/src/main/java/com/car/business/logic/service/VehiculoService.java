package com.car.business.logic.service;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.domain.Vehiculo;
import com.car.business.domain.enums.EstadoVehiculo;
import com.car.business.dto.VehiculoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.VehiculoMapper;
import com.car.business.percistence.repository.VehiculoRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VehiculoService extends BaseService<Vehiculo, VehiculoDto, String> {

    @Autowired
    private CaracteristicaVehiculoService caracteristicaVehiculoService;

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository repository, VehiculoMapper mapper) {
        super(repository, mapper);
        this.vehiculoRepository = repository;
    }

    public List<VehiculoDto> findAvailable() {
        return vehiculoRepository.findAllByEstadoVehiculo(EstadoVehiculo.DISPONIBLE)
                .stream()
                .map(mapper::toDto)
                .toList();
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

    @Override
    protected void postAlta(Vehiculo vehiculo) throws BusinessException {
        try{
            caracteristicaVehiculoService.sumarAuto(vehiculo.getCaracteristicaVehiculo().getId());
        }catch(BusinessException e){
            throw e;
        }
        
    }

    @Override
    protected void postBaja(Vehiculo vehiculo) throws BusinessException{
        try{
            caracteristicaVehiculoService.restarAuto(vehiculo.getCaracteristicaVehiculo().getId());
        }catch(BusinessException e){
            throw e;
        }
    }

}
