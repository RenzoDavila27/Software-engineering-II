package com.contactos.business.persistence.repository;

import java.util.Optional;

import com.contactos.business.domain.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    Optional<Usuario> findByCuentaAndEliminadoFalse(String cuenta);
}
