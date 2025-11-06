package org.consultorio.demo.bussiness.persistance;

import org.consultorio.demo.bussiness.domain.Usuario;
import org.consultorio.demo.bussiness.domain.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String>{

    @Query("SELECT u FROM Usuario u WHERE u.eliminado = false")
    List<Usuario> listAllActives();
    
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    Optional<Usuario> findByNombreUsuarioAndClave(String nombreUsuario, String clave);
    
    List<Usuario> findByRol(Rol rol);

}
