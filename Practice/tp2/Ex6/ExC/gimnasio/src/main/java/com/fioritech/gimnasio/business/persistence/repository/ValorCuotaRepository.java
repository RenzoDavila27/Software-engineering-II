package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.ValorCuota;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorCuotaRepository extends JpaRepository<ValorCuota, String> {

     @Query("SELECT v FROM ValorCuota v WHERE v.fechaHasta IS NULL AND v.eliminado = false")
    public ValorCuota valorcuotaActual();

    @Query("SELECT v FROM ValorCuota v WHERE v.id = :id")
    public ValorCuota buscarValorCuotaPorId(@Param("id")String id);

    @Query("SELECT v FROM ValorCuota v")
    public Collection<ValorCuota> listarValorCuota();

    @Query("SELECT v FROM ValorCuota v WHERE v.eliminado = false ORDER BY v.id DESC")
    public Collection<ValorCuota> listarValorCuotasActivas();

    @Query(value = "SELECT * FROM valores_cuota v WHERE v.eliminado = false AND v.fecha_hasta IS NOT NULL ORDER BY v.fecha_desde DESC LIMIT 1", nativeQuery = true)
    public ValorCuota buscarPenultimaValorCuota();


    @Query("SELECT v FROM ValorCuota v WHERE v.eliminado = false AND v.fechaHasta IS NULL")
    public ValorCuota buscarultimaCuota();
    
}
