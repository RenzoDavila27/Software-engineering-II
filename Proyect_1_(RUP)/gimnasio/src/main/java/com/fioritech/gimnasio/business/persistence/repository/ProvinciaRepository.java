package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Provincia;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, String> {

    @Query("SELECT p FROM Provincia p WHERE p.eliminado = false AND p.pais.eliminado = false")
    public Collection<Provincia> listarProvinciaActiva();

    @Query("SELECT p FROM Provincia p WHERE p.eliminado = false AND p.pais = :id")
    public Collection<Provincia> listarProvinciaPorPais(@Param("id")String id);
}
