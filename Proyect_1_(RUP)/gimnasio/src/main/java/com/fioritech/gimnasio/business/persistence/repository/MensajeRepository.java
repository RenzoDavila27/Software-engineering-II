package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Mensaje;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, String> {

    @Query("SELECT m FROM Mensaje m WHERE Type(m)=Mensaje and m.eliminado = false")
    public List<Mensaje> listarMensajeActivo();
}
