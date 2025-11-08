package com.car.business.percistence.repository;

import com.car.business.domain.Empresa;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends BaseRepository<Empresa, String> {
}
