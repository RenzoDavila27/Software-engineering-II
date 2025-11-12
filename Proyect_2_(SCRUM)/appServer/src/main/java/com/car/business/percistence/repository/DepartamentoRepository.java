package com.car.business.percistence.repository;

import com.car.business.domain.Departamento;
import com.car.business.domain.Pais;
import com.car.business.domain.Provincia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartamentoRepository extends BaseRepository<Departamento, String> {
    Optional<Departamento> findByNombre(String nombre);

    @Query("SELECT p FROM Departamento p WHERE p.nombre = ?1 AND p.provincia = ?2 AND p.eliminado = false")
    Optional<Departamento> findByNombreAndProvincia(String nombre, Provincia provincia);
}
