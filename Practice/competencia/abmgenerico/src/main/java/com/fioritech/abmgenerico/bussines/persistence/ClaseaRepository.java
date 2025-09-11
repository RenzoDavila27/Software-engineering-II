package com.fioritech.abmgenerico.business.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

import com.fioritech.abmgenerico.business.domain.Clasea;

public interface ClaseaRepository extends JpaRepository<Clasea,Long> {

    @Query("SELECT v FROM Clasea v WHERE v.str1 = :str1 AND v.activo = TRUE")
    public Clasea buscarClaseaPorStr1(@Param("str1")String str1);
    
    @Query("SELECT v FROM Clasea v WHERE v.id = :id AND v.activo = TRUE")
    public Clasea buscarClaseaPorId(@Param("id")Long id);

    @Query("SELECT v FROM Clasea v WHERE v.activo = TRUE")
    public Collection<Clasea> listarClaseaActivo();

}
