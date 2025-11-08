package com.car.business.percistence.repository;

import com.car.business.domain.Alquiler;
import org.springframework.stereotype.Repository;

@Repository
public interface AlquilerRepository extends BaseRepository<Alquiler, String> {
}
