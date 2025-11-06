package com.contactos.business.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;

public interface EmpresaRepository extends BaseRepository<Empresa, Long> {

    @EntityGraph(attributePaths = {"contactos", "persona"})
    Optional<Empresa> findWithContactosById(Long id);

    @EntityGraph(attributePaths = {"contactos", "persona"})
    List<Empresa> findByPersonaAndEliminadoFalse(Persona persona);
}
