package com.car.business.percistence.repository;

import com.car.business.domain.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, String> {
}
