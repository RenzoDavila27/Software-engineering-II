package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, String> {
}
