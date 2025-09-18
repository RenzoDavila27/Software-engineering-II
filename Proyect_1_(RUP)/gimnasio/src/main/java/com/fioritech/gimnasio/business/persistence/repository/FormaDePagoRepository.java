package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.FormaDePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaDePagoRepository extends JpaRepository<FormaDePago, String> {
}
