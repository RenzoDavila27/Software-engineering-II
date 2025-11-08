package com.car.business.percistence.repository;

import com.car.business.domain.Factura;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends BaseRepository<Factura, String> {
}
