package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Localidad;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, String> {

    @Query("SELECT l FROM Localidad l WHERE l.eliminado = false AND l.departamento.eliminado = false")
    public Collection<Localidad> listarLocalidadActivo();
}
