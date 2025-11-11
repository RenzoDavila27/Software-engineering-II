package com.car.business.percistence.repository;

import com.car.business.domain.Usuario;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, String> {
    Optional<Usuario> findByNombreUsuarioAndEliminadoFalse(String nombreUsuario);
}
