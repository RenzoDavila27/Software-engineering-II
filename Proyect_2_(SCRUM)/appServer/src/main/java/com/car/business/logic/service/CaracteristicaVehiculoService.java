package com.car.business.logic.service;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.domain.Vehiculo;
import com.car.business.dto.CaracteristicaVehiculoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.CaracteristicaVehiculoMapper;
import com.car.business.percistence.repository.CaracteristicaVehiculoRepository;
import com.car.business.percistence.repository.VehiculoRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CaracteristicaVehiculoService extends BaseService<CaracteristicaVehiculo, CaracteristicaVehiculoDto, String> {

    private final VehiculoRepository vehiculoRepository;

    public CaracteristicaVehiculoService(CaracteristicaVehiculoRepository repository,
                                         CaracteristicaVehiculoMapper mapper,
                                         VehiculoRepository vehiculoRepository) {
        super(repository, mapper);
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    protected void validar(CaracteristicaVehiculo entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La característica del vehículo es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getMarca())) {
            throw new BusinessException("La marca es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getModelo())) {
            throw new BusinessException("El modelo es obligatorio.");
        }
        if (entidad.getAnio() == null || entidad.getAnio() <= 0) {
            throw new BusinessException("El año es obligatorio y debe ser mayor a cero.");
        }
        if (entidad.getCantidadAsientos() <= 0) {
            throw new BusinessException("La cantidad de asientos debe ser mayor a cero.");
        }
        if (entidad.getCantidadPuertas() <= 0) {
            throw new BusinessException("La cantidad de puertas debe ser mayor a cero.");
        }
        if (entidad.getCantidadTotalVehiculosAlquilados() > entidad.getCantidadTotalVehiculos()) {
            throw new BusinessException("Los vehículos alquilados no pueden superar los existentes.");
        }
        if (entidad.getImagen() == null) {
            throw new BusinessException("La imagen es obligatoria.");
        }
    }

    @Transactional
    public void sumarAuto(String id) throws BusinessException{
        Optional<CaracteristicaVehiculo> carac = repository.findById(id);
        if (carac.isPresent()) {
            CaracteristicaVehiculo caracteristica = carac.get();
            caracteristica.setCantidadTotalVehiculos(caracteristica.getCantidadTotalVehiculos() +1);
            repository.save(caracteristica);
            
        }else{
            throw new BusinessException("Error de sistema");
        }

    }

    @Transactional
    public void sumarCantVehiculoAlquilado(String idCaracteristica) throws BusinessException {
         Optional<CaracteristicaVehiculo> carac = repository.findById(idCaracteristica);
        if (carac.isPresent()) {
            CaracteristicaVehiculo caracteristica = carac.get();
            caracteristica.setCantidadTotalVehiculosAlquilados(caracteristica.getCantidadTotalVehiculosAlquilados() +1);
            repository.save(caracteristica);
            
        }else{
            throw new BusinessException("Error de sistema");
        }
    }

    @Transactional
    public void restarCantVehiculoAlquilado(String idCaracteristica) throws BusinessException {
         Optional<CaracteristicaVehiculo> carac = repository.findById(idCaracteristica);
        if (carac.isPresent()) {
            CaracteristicaVehiculo caracteristica = carac.get();
            caracteristica.setCantidadTotalVehiculosAlquilados(caracteristica.getCantidadTotalVehiculosAlquilados() -1);
            repository.save(caracteristica);
            
        }else{
            throw new BusinessException("Error de sistema");
        }
    }


    @Transactional
    public void restarAuto(String id) throws BusinessException{
        Optional<CaracteristicaVehiculo> carac = repository.findById(id);
        if (carac.isPresent()) {
            CaracteristicaVehiculo caracteristica = carac.get();
            caracteristica.setCantidadTotalVehiculos(caracteristica.getCantidadTotalVehiculos() - 1);
            repository.save(caracteristica);
            
        }else{
            throw new BusinessException("Error de sistema");
        }

    }

    @Override
    public List<CaracteristicaVehiculo> listarActivos() throws BusinessException {
        return super.listarActivos();
    }

    @Override
    protected void postBaja(CaracteristicaVehiculo entidad) throws BusinessException {
        if (entidad == null || !StringUtils.hasText(entidad.getId())) {
            return;
        }
        List<Vehiculo> asociados = vehiculoRepository
                .findAllByCaracteristicaVehiculo_IdAndEliminadoFalse(entidad.getId());
        for (Vehiculo vehiculo : asociados) {
            vehiculo.setEliminado(true);
            vehiculoRepository.save(vehiculo);
        }
    }
}
