package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    @Query("SELECT e FROM Empleado e WHERE e.numeroDocumento = :numeroDocumento AND e.eliminado = false")
    public Empleado buscarEmpleadoPorNumeroDocumento(@Param("numeroDocumento")String numeroDocumento);
}
