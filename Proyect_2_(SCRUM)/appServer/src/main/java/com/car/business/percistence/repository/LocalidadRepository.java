package com.car.business.percistence.repository;

import com.car.business.domain.Localidad;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalidadRepository extends BaseRepository<Localidad, String> {
    Optional<Localidad> findByNombre(String nombre);
}
