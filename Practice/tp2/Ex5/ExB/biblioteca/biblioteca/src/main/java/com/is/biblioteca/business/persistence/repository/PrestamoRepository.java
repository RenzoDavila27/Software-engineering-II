package com.is.biblioteca.business.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.is.biblioteca.business.domain.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, String> {

    @Query("SELECT p FROM Prestamo p WHERE p.alta = TRUE")
    List<Prestamo> listarPrestamosActivos();

    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :idUsuario")
    List<Prestamo> listarPrestamosPorUsuario(@Param("idUsuario") String idUsuario);

    @Query("SELECT p FROM Prestamo p WHERE p.libro.id = :idLibro AND p.alta = TRUE")
    Prestamo buscarPrestamoActivoPorLibro(@Param("idLibro") String idLibro);
}
