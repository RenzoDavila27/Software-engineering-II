package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.FormaDePago;
import com.fioritech.gimnasio.business.domain.enums.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FormaDePagoRepository extends JpaRepository<FormaDePago, String> {
    Optional<FormaDePago> findFirstByTipoPagoAndEliminadoFalse(TipoPago tipoPago);
}
