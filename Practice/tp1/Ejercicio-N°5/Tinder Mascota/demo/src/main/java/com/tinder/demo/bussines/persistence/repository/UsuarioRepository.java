package com.tinder.demo.bussines.persistence.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tinder.demo.bussines.domain.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.mail = :mail AND u.fechadebaja IS NULL")
    public Usuario buscarUsuarioPorMail(@Param("mail") String mail);

    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.fechadebaja IS NULL")
    public Usuario buscarUsuarioPorId(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u")
    public Collection<Usuario> buscarUsuarios();

    @Query("SELECT u FROM Usuario u WHERE u.fechadebaja IS NULL")
    public Collection<Usuario> buscarUsuariosActivos();
}
