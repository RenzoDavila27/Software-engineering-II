package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.Mes;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, String> {

    @Query("SELECT c FROM CuotaMensual c WHERE c.eliminado = FALSE")
    public List<CuotaMensual> listarCuotaMensualActiva();
    
}
