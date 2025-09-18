package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, String> {
}
