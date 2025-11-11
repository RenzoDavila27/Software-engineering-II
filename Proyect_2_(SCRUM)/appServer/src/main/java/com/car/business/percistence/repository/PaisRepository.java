package com.car.business.percistence.repository;

import com.car.business.domain.Pais;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends BaseRepository<Pais, String> {
    Optional<Pais> findByNombre(String nombre);
}
