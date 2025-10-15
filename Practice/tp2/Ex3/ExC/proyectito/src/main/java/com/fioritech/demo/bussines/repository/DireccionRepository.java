package com.fioritech.demo.bussines.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Direccion;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DireccionRepository extends JpaRepository<Direccion, Long>{

    @Query("SELECT d FROM Direccion d WHERE d.eliminado = false")
    public Collection<Direccion> buscarDireccionesActivas();

    @Query("SELECT d FROM Direccion d WHERE d.id = :id AND d.eliminado = false")
    public Optional<Direccion> findById(@Param("id") Long id);


}