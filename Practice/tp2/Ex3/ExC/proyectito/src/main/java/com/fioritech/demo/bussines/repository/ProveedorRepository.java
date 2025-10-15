package com.fioritech.demo.bussines.repository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Proveedor;


public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    @Query("SELECT p FROM Proveedor p WHERE p.eliminado = false")
    public Collection<Proveedor> buscarProveedoresActivos();

    @Query("SELECT p FROM Proveedor p WHERE p.id = :id AND p.eliminado = false")
    public Optional<Proveedor> findById(@Param("id") Long id);

    @Query("SELECT p FROM Proveedor p WHERE p.cuit = :cuit AND p.eliminado = false")
    public Optional<Proveedor> findByCuit(@Param("cuit") String cuit);
}
