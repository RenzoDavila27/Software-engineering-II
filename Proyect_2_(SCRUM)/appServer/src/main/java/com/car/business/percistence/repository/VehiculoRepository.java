package com.car.business.percistence.repository;

import com.car.business.domain.Vehiculo;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, String> {
}
