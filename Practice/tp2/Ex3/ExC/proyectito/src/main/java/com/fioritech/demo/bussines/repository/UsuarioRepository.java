package com.fioritech.demo.bussines.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fioritech.demo.bussines.domain.Usuario;  
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.eliminado = false")
    public Collection<Usuario> buscarUsuariosActivos();

    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.eliminado = false")
    public Optional<Usuario> findById(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u WHERE u.cuenta = :cuenta AND u.eliminado = false")
    public Optional<Usuario> findByCuenta(@Param("cuenta") String cuenta);

}
