package com.example.mecanic.bussines.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.mecanic.bussines.domain.entity.Vehiculo;


public interface VehiculoRepository extends BaseRepository<Vehiculo,Long> {
    
    @Query("SELECT v FROM Vehiculo v WHERE v.patente = :patente AND v.eliminado = false")
    Vehiculo buscarVehiculoPorPatente(@Param("patente") String patente);
}
