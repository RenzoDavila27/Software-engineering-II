package com.tienda.app.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.Imagen;

public interface ImagenRepository extends BaseRepository<Imagen, Long> {

    @Query("SELECT i "
         + "  FROM Imagen i "
         + " WHERE LOWER(i.nombre) = LOWER(:nombre) "
         + "   AND i.eliminado = FALSE")
    Imagen buscarImagenPorNombre(@Param("nombre") String nombre);
}
