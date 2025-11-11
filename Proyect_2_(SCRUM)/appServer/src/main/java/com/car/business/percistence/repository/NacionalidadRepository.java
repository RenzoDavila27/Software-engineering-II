package com.car.business.percistence.repository;

import com.car.business.domain.Nacionalidad;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NacionalidadRepository extends BaseRepository<Nacionalidad, String> {
    Optional<Nacionalidad> findByNombre(String nombre);
}
