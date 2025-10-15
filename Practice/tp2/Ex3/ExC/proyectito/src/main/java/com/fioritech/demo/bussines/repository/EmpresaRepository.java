package com.fioritech.demo.bussines.repository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Empresa;


public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    @Query("SELECT e FROM Empresa e WHERE e.eliminado = false")
    public Collection<Empresa> buscarEmpresasActivas();

    @Query("SELECT e FROM Empresa e WHERE e.id = :id AND e.eliminado = false")
    public Optional<Empresa> findById(@Param("id") Long id);

    @Query("SELECT e FROM Empresa e WHERE e.razonSocial = :razonSocial AND e.eliminado = false")
    public Optional<Empresa> findByRazonSocial(@Param("razonSocial") String razonSocial);
}
