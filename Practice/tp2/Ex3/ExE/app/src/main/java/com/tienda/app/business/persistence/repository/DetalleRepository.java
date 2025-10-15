package com.tienda.app.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.Detalle;

public interface DetalleRepository extends BaseRepository<Detalle, Long> {

    @Query("SELECT d "
         + "  FROM Detalle d "
         + " WHERE d.articulo.id = :articuloId "
         + "   AND d.imagen.id = :imagenId "
         + "   AND d.eliminado = FALSE")
    Detalle buscarDetallePorArticuloEImagen(@Param("articuloId") Long articuloId,
                                            @Param("imagenId") Long imagenId);
}
