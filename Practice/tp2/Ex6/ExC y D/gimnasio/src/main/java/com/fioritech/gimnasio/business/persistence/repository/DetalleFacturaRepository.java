package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, String> {
}
