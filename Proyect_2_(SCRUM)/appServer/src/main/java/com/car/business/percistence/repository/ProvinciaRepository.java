package com.car.business.percistence.repository;

import com.car.business.domain.Pais;
import com.car.business.domain.Provincia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinciaRepository extends BaseRepository<Provincia, String> {
    Optional<Provincia> findByNombre(String nombre);

    @Query("SELECT p FROM Provincia p WHERE p.nombre = ?1 AND p.pais = ?2 AND p.eliminado = false")
    Optional<Provincia> findByNombreAndPais(String nombre, Pais pais);
}
