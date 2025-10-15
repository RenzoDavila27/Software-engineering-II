package com.tienda.app.business.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.CarritoItem;

public interface CarritoItemRepository extends BaseRepository<CarritoItem, Long> {

    @Query("SELECT ci "
         + "  FROM CarritoItem ci "
         + " WHERE ci.carrito.id = :carritoId "
         + "   AND ci.articulo.id = :articuloId "
         + "   AND ci.eliminado = FALSE")
    CarritoItem buscarItemPorCarritoYArticulo(@Param("carritoId") Long carritoId,
                                              @Param("articuloId") Long articuloId);

    @Query("SELECT ci "
         + "  FROM CarritoItem ci "
         + " WHERE ci.carrito.id = :carritoId "
         + "   AND ci.eliminado = FALSE")
    List<CarritoItem> listarPorCarrito(@Param("carritoId") Long carritoId);
}
