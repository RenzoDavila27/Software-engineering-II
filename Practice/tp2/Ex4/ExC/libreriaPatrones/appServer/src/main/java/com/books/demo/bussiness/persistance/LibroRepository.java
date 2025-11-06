package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.Libro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends BaseRepository<Libro> {

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores WHERE l.id = :id")
    Optional<Libro> buscarPorId(@Param("id") Long id);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores WHERE l.eliminado = false")
    List<Libro> listarLibrosActivos();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores WHERE l.persona IS NULL and l.eliminado = false")
    List<Libro> buscarLibrosSinAsignar();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores WHERE l.persona IS NOT NULL and l.eliminado = false")
    List<Libro> buscarLibrosAsignados();

    @Query("""
            SELECT DISTINCT l FROM Libro l
            LEFT JOIN FETCH l.persona
            LEFT JOIN FETCH l.autores
            WHERE l.eliminado = false
              AND (
                   LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
                OR LOWER(l.titulo) = LOWER(:titulo)
                  )
            """)
    List<Libro> buscarPorTitulo(@Param("titulo") String titulo);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores WHERE l.eliminado = false AND LOWER(l.genero) LIKE LOWER(CONCAT('%', :genero, '%'))")
    List<Libro> buscarPorGenero(@Param("genero") String genero);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.persona LEFT JOIN FETCH l.autores autor WHERE l.eliminado = false AND autor.id = :autorId")
    List<Libro> buscarPorAutorId(@Param("autorId") Long autorId);

    @Query("""
            SELECT DISTINCT l FROM Libro l
            LEFT JOIN FETCH l.persona
            LEFT JOIN FETCH l.autores autor
            WHERE l.eliminado = false
              AND (
                    LOWER(autor.nombre) LIKE LOWER(CONCAT('%', :autorNombre, '%'))
                 OR LOWER(autor.apellido) LIKE LOWER(CONCAT('%', :autorNombre, '%'))
                 OR LOWER(CONCAT(autor.nombre, ' ', autor.apellido)) LIKE LOWER(CONCAT('%', :autorNombre, '%'))
                 OR LOWER(CONCAT(autor.apellido, ' ', autor.nombre)) LIKE LOWER(CONCAT('%', :autorNombre, '%'))
                 OR LOWER(CONCAT(autor.nombre, ' ', autor.apellido)) = LOWER(:autorNombre)
                 OR LOWER(CONCAT(autor.apellido, ' ', autor.nombre)) = LOWER(:autorNombre)
                  )
            """)
    List<Libro> buscarPorAutorNombre(@Param("autorNombre") String autorNombre);
}
