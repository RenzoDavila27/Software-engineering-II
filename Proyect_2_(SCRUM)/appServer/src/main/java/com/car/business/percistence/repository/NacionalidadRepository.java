package com.car.business.percistence.repository;

import com.car.business.domain.Nacionalidad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NacionalidadRepository extends BaseRepository<Nacionalidad, String> {
    @Query("SELECT n FROM Nacionalidad n WHERE n.nombre = ?1 and n.eliminado = false")
    Optional<Nacionalidad> findByNombre(String nombre);
}
