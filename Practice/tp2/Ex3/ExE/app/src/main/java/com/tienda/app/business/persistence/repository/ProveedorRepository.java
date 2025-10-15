package com.tienda.app.business.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tienda.app.business.domain.Proveedor;

public interface ProveedorRepository extends BaseRepository<Proveedor, Long> {

    @Query("SELECT p "
         + "  FROM Proveedor p "
         + " WHERE LOWER(p.nombre) = LOWER(:nombre) "
         + "   AND p.eliminado = FALSE")
    Proveedor buscarProveedorPorNombre(@Param("nombre") String nombre);
}
