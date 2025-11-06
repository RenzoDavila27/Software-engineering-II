package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.Localidad;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalidadRepository extends BaseRepository<Localidad> {

    @Query("SELECT l FROM Localidad l WHERE l.id = :id")
    Optional<Localidad> buscarPorId(@Param("id") Long id);

    @Query("SELECT l FROM Localidad l WHERE l.eliminado = false")
    List<Localidad> listarLocalidadesActivas();
}
