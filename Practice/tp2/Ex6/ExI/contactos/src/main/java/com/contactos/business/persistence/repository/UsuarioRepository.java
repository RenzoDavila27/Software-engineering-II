package com.contactos.business.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

import com.contactos.business.domain.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    @EntityGraph(attributePaths = {"persona"})
    Optional<Usuario> findByCuentaAndEliminadoFalse(String cuenta);
}
