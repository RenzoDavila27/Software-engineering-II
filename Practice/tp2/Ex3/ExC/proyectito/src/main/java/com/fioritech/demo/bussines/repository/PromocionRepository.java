package com.fioritech.demo.bussines.repository;

import com.fioritech.demo.bussines.domain.Promocion;
import com.fioritech.demo.bussines.domain.PromocionTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    @Query("SELECT p FROM Promocion p WHERE p.eliminado = false")
    Collection<Promocion> buscarPromocionesActivas();

    @Query("SELECT p FROM Promocion p WHERE p.id = :id AND p.eliminado = false")
    Optional<Promocion> findById(@Param("id") Long id);

    @Query("SELECT p FROM Promocion p WHERE LOWER(p.titulo) = LOWER(:titulo) AND p.eliminado = false")
    Optional<Promocion> findByTituloIgnoreCase(@Param("titulo") String titulo);

    @Query("SELECT p FROM Promocion p WHERE p.eliminado = false AND p.tipo = :tipo")
    Collection<Promocion> buscarActivasPorTipo(@Param("tipo") PromocionTipo tipo);

    Optional<Promocion> findFirstByTipoAndEliminadoFalse(PromocionTipo tipo);
}
