package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.Autor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends BaseRepository<Autor> {

    @Query("SELECT a FROM Autor a WHERE a.id = :id")
    Optional<Autor> buscarPorId(@Param("id") Long id);

    @Query("SELECT a FROM Autor a WHERE a.eliminado = false")
    List<Autor> listarAutoresActivos();

    @Query("""
            SELECT a FROM Autor a
            WHERE a.eliminado = false AND (
                     LOWER(a.nombre) LIKE LOWER(CONCAT('%', :criterio, '%'))
                  OR LOWER(a.apellido) LIKE LOWER(CONCAT('%', :criterio, '%'))
                  OR LOWER(CONCAT(a.nombre, ' ', a.apellido)) LIKE LOWER(CONCAT('%', :criterio, '%'))
                  OR LOWER(CONCAT(a.apellido, ' ', a.nombre)) LIKE LOWER(CONCAT('%', :criterio, '%'))
                  OR LOWER(CONCAT(a.nombre, ' ', a.apellido)) = LOWER(:criterio)
                  OR LOWER(CONCAT(a.apellido, ' ', a.nombre)) = LOWER(:criterio)
            )
            """)
    List<Autor> buscarPorNombreSimilar(@Param("criterio") String criterio);
}
