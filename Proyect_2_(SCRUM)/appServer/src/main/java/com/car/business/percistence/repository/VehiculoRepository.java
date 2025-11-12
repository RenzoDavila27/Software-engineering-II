package com.car.business.percistence.repository;

import com.car.business.domain.Vehiculo;
import com.car.business.domain.enums.EstadoVehiculo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, String> {
    List<Vehiculo> findAllByEstadoVehiculo(EstadoVehiculo estado);
    List<Vehiculo> findAllByCaracteristicaVehiculo_IdAndEliminadoFalse(String caracteristicaId);
}
