package com.example.mecanic.bussines.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mecanic.bussines.domain.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    @Query ("SELECT u "
    	  + "  FROM Usuario u "
    	  + " WHERE u.nombre = :nombre"
    	  + "   AND u.eliminado = FALSE")
    public Usuario buscarUsuarioPorNombre (@Param ("nombre") String nombre);
    
    @Query ("SELECT u "
    	  + "  FROM Usuario u "
    	  + " WHERE u.eliminado = FALSE")
    public List<Usuario> listarUsuarioActivo ();
    
    @Query("SELECT u "
         + "  FROM Usuario u "
         + " WHERE u.nombre = :nombre "
         + "   AND u.clave = :clave"
         + "   AND u.eliminado = FALSE")
    public Usuario buscarUsuarioPorNombreYClave(@Param("nombre")String nombre, @Param("clave")String clave);
}

