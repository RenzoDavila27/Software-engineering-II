package com.contactos.business.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import com.contactos.business.domain.Persona;

public interface PersonaRepository extends BaseRepository<Persona, Long> {

    @EntityGraph(attributePaths = {"empresas", "empresas.contactos", "contactos", "usuarios"})
    Optional<Persona> findWithRelationshipsById(Long id);

    @Query("select p from Persona p where p.eliminado = false or p.eliminado is null")
    List<Persona> findAllActivas();
}
