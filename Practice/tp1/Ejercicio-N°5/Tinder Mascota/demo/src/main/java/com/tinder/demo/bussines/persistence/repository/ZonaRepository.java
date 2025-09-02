package com.tinder.demo.bussines.persistence.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tinder.demo.bussines.domain.Zona;

public interface ZonaRepository extends JpaRepository<Zona,Long>{

    @Query("SELECT z FROM Zona z WHERE z.eliminado = false")
    public Collection<Zona> listarZonasActivas();

    @Query("SELECT z FROM Zona z WHERE z.id = :id and z.eliminado = false")
    public Zona buscarZonaActiva(@Param("id")Long id);

    @Query("SELECT z FROM Zona z WHERE z.nombre = :nombre AND z.eliminado = false")
    public Zona buscarZonaPorNombre(@Param("nombre")String nombre);
    
}
