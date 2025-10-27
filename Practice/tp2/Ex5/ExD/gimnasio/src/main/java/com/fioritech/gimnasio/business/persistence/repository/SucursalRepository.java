package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Sucursal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, String> {

    @Query("SELECT s FROM Sucursal s WHERE s.nombre = :nombre AND s.eliminado = false")
    public Sucursal buscarSucursalPorNombre(@Param("nombre")String nombre);

    @Query("SELECT s FROM Sucursal s WHERE s.id = :id AND s.eliminado = false")
    public Sucursal buscarSucursalPorId(@Param("id")String id);
}
