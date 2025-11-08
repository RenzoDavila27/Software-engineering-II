package com.car.business.percistence.repository;

import com.car.business.domain.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, String> {
}
