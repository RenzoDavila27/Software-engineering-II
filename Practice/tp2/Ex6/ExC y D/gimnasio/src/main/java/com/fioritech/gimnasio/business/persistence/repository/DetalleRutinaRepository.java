package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.DetalleRutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRutinaRepository extends JpaRepository<DetalleRutina, String> {
}
