package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.Domicilio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DomicilioRepository extends BaseRepository<Domicilio> {

    @Query("SELECT d FROM Domicilio d LEFT JOIN FETCH d.localidad WHERE d.id = :id")
    Optional<Domicilio> buscarPorId(@Param("id") Long id);

    @Query("SELECT d FROM Domicilio d LEFT JOIN FETCH d.localidad WHERE d.eliminado = false")
    List<Domicilio> listarDomiciliosActivos();
}
