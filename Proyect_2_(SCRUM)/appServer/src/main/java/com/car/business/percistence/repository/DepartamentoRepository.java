package com.car.business.percistence.repository;

import com.car.business.domain.Departamento;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartamentoRepository extends BaseRepository<Departamento, String> {
    Optional<Departamento> findByNombre(String nombre);
}
