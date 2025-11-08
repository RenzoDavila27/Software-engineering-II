package com.car.business.percistence.repository;

import com.car.business.domain.Empleado;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends BaseRepository<Empleado, String> {
}
