package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Rutina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, String> {
    List<Rutina> findBySocioIdAndEliminadoFalse(String socioId);
}
