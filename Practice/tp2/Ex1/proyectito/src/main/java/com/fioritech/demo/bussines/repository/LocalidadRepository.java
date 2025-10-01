package com.fioritech.demo.bussines.repository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Localidad;

public interface LocalidadRepository extends JpaRepository<Localidad, Long>{

    @Query("SELECT l FROM Localidad l WHERE l.eliminado = false")
    public Collection<Localidad> buscarLocalidadesActivas();

    @Query("SELECT l FROM Localidad l WHERE l.id = :id AND l.eliminado = false")
    public Optional<Localidad> findById(@Param("id") Long id);

}
