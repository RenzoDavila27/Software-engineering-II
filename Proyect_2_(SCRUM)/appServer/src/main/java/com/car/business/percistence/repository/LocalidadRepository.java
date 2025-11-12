package com.car.business.percistence.repository;

import com.car.business.domain.Departamento;
import com.car.business.domain.Localidad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalidadRepository extends BaseRepository<Localidad, String> {
    Optional<Localidad> findByNombre(String nombre);
    @Query("SELECT p FROM Localidad p WHERE p.nombre = ?1 AND p.codigoPostal = ?2 AND p.departamento = ?3 AND p.eliminado = false")
    Optional<Localidad> findByNombreAndCodigoPostalAndDepartamento(String nombre, String codigoPostal, Departamento departamento);
}
