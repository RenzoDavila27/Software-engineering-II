package com.example.demo.business.persistence.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.business.domain.Estudio;

public interface EstudioRepository extends BaseRepository<Estudio> {

    @Query("SELECT p FROM Estudio p WHERE p.nombre = :nombre AND p.eliminado = FALSE")
    Estudio buscarEstudioPorNombre(@Param("nombre") String nombre);

    @Query("SELECT v FROM Estudio v WHERE v.id = :id AND v.eliminado = FALSE")
    Estudio buscarEstudioPorId(@Param("id") Long id);

    @Query("SELECT p FROM Estudio p WHERE p.eliminado = FALSE")
    Collection<Estudio> listarEstudioActivo();
}
