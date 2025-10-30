package com.example.mecanic.bussines.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.mecanic.bussines.domain.entity.HistorialArreglo;

public interface HistorialArregloRepository extends BaseRepository<HistorialArreglo,Long> {
    
    @Query("SELECT h FROM HistorialArreglo h WHERE h.vehiculo.id = :idVehiculo AND h.eliminado = false ORDER BY h.fechaArreglo")
    public List<HistorialArreglo> buscarHistorialArregloPorIdVehiculo(@Param("idVehiculo")Long idVehiculo);
}
