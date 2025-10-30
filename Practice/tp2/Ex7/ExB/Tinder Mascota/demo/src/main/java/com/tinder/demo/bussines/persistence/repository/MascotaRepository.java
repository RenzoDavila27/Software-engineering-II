package com.tinder.demo.bussines.persistence.repository;

import com.tinder.demo.bussines.domain.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface MascotaRepository extends JpaRepository<Mascota,Long> {

    @Query("SELECT m FROM Mascota m WHERE m.id = :id")
    Mascota buscarMascotaPorId(@Param("id") Long id);

    @Query("SELECT m FROM Mascota m WHERE m.fechadebaja IS NULL")
    Collection<Mascota> buscarMascotasActivas();

    @Query("SELECT m FROM Mascota m WHERE m.usuario.id= :idUsuario AND m.fechadebaja IS NULL")
    Collection<Mascota> buscarMascotasPorUsuarioActivas(@Param("idUsuario") Long id);

    @Query("SELECT m FROM Mascota m WHERE m.usuario.id= :idUsuario AND m.fechadebaja IS NOT NULL")
    Collection<Mascota> buscarMascotasPorUsuarioInactivas(@Param("idUsuario") Long id);

    
}
