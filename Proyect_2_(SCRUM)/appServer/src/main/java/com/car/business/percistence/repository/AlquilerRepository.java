package com.car.business.percistence.repository;

import com.car.business.domain.Alquiler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlquilerRepository extends BaseRepository<Alquiler, String> {

    @Query("SELECT a FROM Alquiler a WHERE a.eliminado = false AND a.fechaHasta = :maniana")
    List<Alquiler> buscarAlquilerVecManiana(@Param("maniana") LocalDate maniana);

    @Query("SELECT a FROM Alquiler a WHERE a.eliminado = false AND a.fechaDesde = :hoy")
    List<Alquiler> buscarAlquilerEmpHoy(@Param("hoy") LocalDate hoy);

}
