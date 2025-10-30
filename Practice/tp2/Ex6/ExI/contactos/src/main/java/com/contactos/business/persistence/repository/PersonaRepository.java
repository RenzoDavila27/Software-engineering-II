package com.contactos.business.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

import com.contactos.business.domain.Persona;

public interface PersonaRepository extends BaseRepository<Persona, Long> {

    @EntityGraph(attributePaths = {"empresas", "contactos", "usuarios"})
    Optional<Persona> findWithRelationshipsById(Long id);

    @EntityGraph(attributePaths = {"empresas", "contactos", "usuarios"})
    List<Persona> findAllByEliminadoFalse();
}
