package com.car.business.percistence.repository;

import com.car.business.domain.Persona;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends BaseRepository<Persona, String> {
}
