package com.fioritech.demo.bussines.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fioritech.demo.bussines.domain.Pais;

public interface PaisRepository extends JpaRepository<Pais, Long> {

    @Query("SELECT p FROM Pais p WHERE p.eliminado = false")
    public Collection<Pais> buscarPaisesActivos();

    @Query("SELECT p FROM Pais p WHERE p.id = :id AND p.eliminado = false")
    public Optional<Pais> findById(@Param("id") Long id);

    Optional<Pais> findByNombreIgnoreCaseAndEliminadoFalse(String nombre);

}
