package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocioRepository extends JpaRepository<Socio, String> {

    @Query("SELECT COUNT(s.id) FROM Socio s")
    public Long obtenerProximoNumeroSocio();

    @Query("SELECT s FROM Socio s WHERE FUNCTION('MONTH', s.fechaNacimiento) = :mes AND FUNCTION('DAY', s.fechaNacimiento) = :dia")
    public Collection<Socio> listarCumpleanieros(@Param("dia") int dia, @Param("mes") int mes);

    @Query("SELECT s FROM Socio s WHERE s.eliminado = false")
    public Collection<Socio> listarSociosActivos();

    @Query("SELECT DISTINCT s FROM Socio s JOIN s.cuotasMensuales c WHERE c.estado = :estado")
    public Collection<Socio> SocioConDeudas(@Param("estado") EstadoCuotaMensual estado);

    Optional<Socio> findByUsuarioIdAndEliminadoFalse(String usuarioId);
}
