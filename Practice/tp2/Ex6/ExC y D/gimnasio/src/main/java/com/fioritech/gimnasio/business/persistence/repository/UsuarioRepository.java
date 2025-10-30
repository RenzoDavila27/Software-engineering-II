package com.fioritech.gimnasio.business.persistence.repository;

import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    @Query("SELECT u FROM Usuario u WHERE u.eliminado = false AND u.rol = :tipo")
    public Collection<Usuario> listarUsuariosPorTipo(@Param("tipo")RolUsuario tipo);

    @Query("SELECT u FROM Usuario u WHERE u.nombreUsuario = :cuenta AND u.clave = :clave AND u.eliminado = false")
    public Usuario buscarUsuarioPorCuentaYClave(@Param("cuenta")String cuenta, @Param("clave")String clave);

    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);
}
