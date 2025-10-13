package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.Libro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores LEFT JOIN FETCH l.archivoLibro WHERE l.id = :id")
    Optional<Libro> buscarPorId(@Param("id") Long id);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores LEFT JOIN FETCH l.archivoLibro WHERE l.eliminado = false")
    List<Libro> listarLibrosActivos();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores LEFT JOIN FETCH l.archivoLibro WHERE l.persona IS NULL and l.eliminado = false")
    List<Libro> buscarLibrosSinAsignar();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores LEFT JOIN FETCH l.archivoLibro WHERE l.persona IS NOT NULL and l.eliminado = false")
    List<Libro> buscarLibrosAsignados();
}
