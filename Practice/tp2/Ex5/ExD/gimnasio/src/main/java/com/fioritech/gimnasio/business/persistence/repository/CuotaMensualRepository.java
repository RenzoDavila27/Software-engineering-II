package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, String> {

    @Query("SELECT c FROM CuotaMensual c WHERE c.eliminado = FALSE")
    public List<CuotaMensual> listarCuotaMensualActiva();

    @Query("SELECT c FROM CuotaMensual c WHERE c.socio.id = :idSocio AND c.estado = :estado AND c.eliminado = FALSE")
    public Collection<CuotaMensual> listarDeudasPorSocio(@Param("idSocio")String idSocio,@Param("estado") EstadoCuotaMensual estado);
    
    @Query("SELECT c FROM CuotaMensual c WHERE c.fechaVencimiento = (SELECT MAX(c2.fechaVencimiento) FROM CuotaMensual c2 WHERE c2.socio.id = c.socio.id) AND c.socio.eliminado = false")
    public Collection<CuotaMensual> ultimaCuotaDeSocio();

    @Query("SELECT c FROM CuotaMensual c WHERE c.socio.numeroDocumento = :dni AND c.socio.eliminado = false AND c.eliminado = false")
    public Collection<CuotaMensual> buscarCuotasDeSocioPorDNI(@Param("dni")String dni);

    @Query("Select c FROM CuotaMensual c where c.socio.usuario.id = :idUsuario AND c.socio.eliminado = false AND c.eliminado = false")
    public Collection<CuotaMensual> buscarCuotasDeSocioPorUsuario(@Param("idUsuario")String idUsuario);

}   
