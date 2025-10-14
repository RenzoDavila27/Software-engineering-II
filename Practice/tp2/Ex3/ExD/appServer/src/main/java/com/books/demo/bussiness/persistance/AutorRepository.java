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
}
