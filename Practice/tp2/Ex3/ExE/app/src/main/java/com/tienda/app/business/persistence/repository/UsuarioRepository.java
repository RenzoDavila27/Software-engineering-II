package com.tienda.app.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario, Long> {

    @Query("SELECT u "
         + "  FROM Usuario u "
         + " WHERE LOWER(u.nombre) = LOWER(:nombre) "
         + "   AND u.eliminado = FALSE")
    Usuario buscarUsuarioPorNombre(@Param("nombre") String nombre);
}
