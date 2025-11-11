package com.car.business.percistence.repository;

import com.car.business.domain.Provincia;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinciaRepository extends BaseRepository<Provincia, String> {
    Optional<Provincia> findByNombre(String nombre);
}
