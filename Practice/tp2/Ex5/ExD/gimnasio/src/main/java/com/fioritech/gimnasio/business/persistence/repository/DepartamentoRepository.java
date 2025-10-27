package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Departamento;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, String> {

    @Query("SELECT d FROM Departamento d WHERE d.eliminado = false AND d.provincia.eliminado = false")
    public Collection<Departamento> listarDepartamentoActivo();
}
