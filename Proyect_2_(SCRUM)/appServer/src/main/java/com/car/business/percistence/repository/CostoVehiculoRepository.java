package com.car.business.percistence.repository;

import com.car.business.domain.CostoVehiculo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CostoVehiculoRepository extends BaseRepository<CostoVehiculo, String> {

    @Query("SELECT c FROM CostoVehiculo c WHERE c.caracteristicaVehiculo.id = :id AND c.fechaHasta = :fechaHasta")
    CostoVehiculo buscarCostoVehiculoActual(@Param("id") String idCaracteristica, @Param("fechaHasta") LocalDate fechaHasta);

    @Query("SELECT c FROM CostoVehiculo c WHERE c.caracteristicaVehiculo.id = :id AND c.eliminado = false")
    List<CostoVehiculo> listarCostosPorVehiculo(@Param("id") String id);
}
