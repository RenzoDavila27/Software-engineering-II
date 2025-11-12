package com.car.business.percistence.repository;

import com.car.business.domain.Pais;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends BaseRepository<Pais, String> {

    @Query("SELECT p FROM Pais p WHERE p.nombre = ?1 and p.eliminado = false")
    Optional<Pais> findByNombre(String nombre);
}
