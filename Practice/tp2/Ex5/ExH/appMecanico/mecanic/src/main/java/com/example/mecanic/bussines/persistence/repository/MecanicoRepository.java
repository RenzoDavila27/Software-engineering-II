/*

package com.example.mecanic.bussines.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.mecanic.bussines.domain.entity.Mecanico;

public interface MecanicoRepository extends BaseRepository<Mecanico,Long> {
    
    @Query("SELECT m FROM Mecanico m WHERE m.legajo = :legajo AND m.eliminado = false")
    public Mecanico buscarMecanicoPorLegajo(@Param("legajo")String legajo);


}
*/