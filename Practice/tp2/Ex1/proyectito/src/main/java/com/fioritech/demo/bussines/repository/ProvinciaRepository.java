package com.fioritech.demo.bussines.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Provincia;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {

    @Query("SELECT p FROM Provincia p WHERE p.eliminado = false")
    public Collection<Provincia> buscarProvinciasActivas();

    @Query("SELECT p FROM Provincia p WHERE p.id = :id AND p.eliminado = false")
    public Optional<Provincia> findById(@Param("id") Long id);

    
}
    
