package com.tienda.app.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.Articulo;

public interface ArticuloRepository extends BaseRepository<Articulo, Long> {

    @Query("SELECT a "
         + "  FROM Articulo a "
         + " WHERE LOWER(a.nombre) = LOWER(:nombre) "
         + "   AND a.eliminado = FALSE")
    Articulo buscarArticuloPorNombre(@Param("nombre") String nombre);
}
