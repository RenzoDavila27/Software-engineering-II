package com.books.demo.bussiness.persistance;

import com.books.demo.bussiness.domain.Persona;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @Query("SELECT p FROM Persona p LEFT JOIN FETCH p.domicilio WHERE p.id = :id")
    Optional<Persona> buscarPorId(@Param("id") Long id);

    @Query("SELECT p FROM Persona p LEFT JOIN FETCH p.domicilio WHERE p.eliminado = false")
    List<Persona> listarPersonasActivas();
}
