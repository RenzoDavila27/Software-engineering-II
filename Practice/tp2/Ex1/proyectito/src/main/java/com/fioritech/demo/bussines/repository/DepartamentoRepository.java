package com.fioritech.demo.bussines.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Departamento;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    @Query("SELECT d FROM Departamento d WHERE d.eliminado = false")
    public Collection<Departamento> buscarDepartamentosActivos();

    @Query("SELECT d FROM Departamento d WHERE d.id = :id AND d.eliminado = false")
    public Optional<Departamento> findById(@Param("id") Long id);

}
