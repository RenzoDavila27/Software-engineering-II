package com.example.demo.business.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

import com.example.demo.business.domain.Videojuego;

public interface VideojuegoRepository extends JpaRepository<Videojuego,Long> {

    @Query("SELECT v FROM Videojuego v WHERE v.titulo = :titulo AND v.activo = TRUE")
    public Videojuego buscarVideojuegoPorNombre(@Param("titulo")String titulo);
    
    @Query("SELECT v FROM Videojuego v WHERE v.id = :id AND v.activo = TRUE")
    public Videojuego buscarVideojuegoPorId(@Param("id")Long id);

    @Query("SELECT v FROM Videojuego v WHERE v.activo = TRUE")
    public Collection<Videojuego> listarVideojuegoActivo();

}
