package com.tienda.app.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.Carrito;

public interface CarritoRepository extends BaseRepository<Carrito, Long> {

    @Query("SELECT c "
         + "  FROM Carrito c "
         + " WHERE c.usuario.id = :usuarioId "
         + "   AND c.eliminado = FALSE")
    Carrito buscarCarritoActivoPorUsuario(@Param("usuarioId") Long usuarioId);
}
