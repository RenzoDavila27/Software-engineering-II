package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, String> {
}
