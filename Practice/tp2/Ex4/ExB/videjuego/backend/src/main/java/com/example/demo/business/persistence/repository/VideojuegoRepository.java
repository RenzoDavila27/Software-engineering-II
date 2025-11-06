package com.example.demo.business.persistence.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.business.domain.Videojuego;

public interface VideojuegoRepository extends BaseRepository<Videojuego> {

    @Query("SELECT v FROM Videojuego v WHERE v.titulo = :titulo AND v.eliminado = FALSE")
    Videojuego buscarVideojuegoPorNombre(@Param("titulo") String titulo);
    
    @Query("SELECT v FROM Videojuego v WHERE v.id = :id AND v.eliminado = FALSE")
    Videojuego buscarVideojuegoPorId(@Param("id") Long id);

    @Query("SELECT v FROM Videojuego v WHERE v.eliminado = FALSE")
    Collection<Videojuego> listarVideojuegoActivo();

}
