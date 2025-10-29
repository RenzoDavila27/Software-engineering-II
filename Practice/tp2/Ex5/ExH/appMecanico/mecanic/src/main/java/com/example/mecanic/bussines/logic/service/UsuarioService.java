package com.example.mecanic.bussines.logic.service;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.example.mecanic.bussines.domain.entity.Usuario;
import com.example.mecanic.bussines.domain.enumeration.Rol;
import com.example.mecanic.bussines.persistence.repository.UsuarioRepository;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;

@Service
public class UsuarioService implements UserDetailsService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario alta(String nombre, String clave, String clave2) throws ErrorServiceException {
        try{
            validar(nombre, clave, clave2);
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setClave(new BCryptPasswordEncoder().encode(clave));
            usuario.setRol(Rol.MECANICO);
            usuario.setEliminado(false);
            usuarioRepository.save(usuario);
            return usuario;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al dar de alta el usuario");
        }
    }

    public Usuario modificar(Long id, String nombre,String clave, String clave2) throws ErrorServiceException {
        try {
            validar(nombre, clave, clave2);
            Optional<Usuario> respuesta = usuarioRepository.findById(id);
            if (respuesta.isPresent()) {
                
                Usuario usuario = respuesta.get();
                usuario.setNombre(nombre);
                usuario.setClave(new BCryptPasswordEncoder().encode(clave));
                usuarioRepository.save(usuario);
                return usuario;
            } else {
                throw new ErrorServiceException("No se encontró el usuario solicitado");
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar el usuario");
        }
    }

    public void eliminar(Long id) throws ErrorServiceException {
        try {
            Optional<Usuario> respuesta = usuarioRepository.findById(id);
            if (respuesta.isPresent()) {
                Usuario usuario = respuesta.get();
                usuario.setEliminado(true);
                usuarioRepository.save(usuario);
            } else {
                throw new ErrorServiceException("No se encontró el usuario solicitado");
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al eliminar el usuario");
        }
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.listarUsuarioActivo();
    }

    public void validar(String nombre, String clave, String clave2) throws ErrorServiceException {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServiceException("El nombre de usuario no puede ser nulo o estar vacio");
        }
        if (clave == null || clave.isEmpty() || clave.length() < 6) {
            throw new ErrorServiceException("La clave no puede ser nula, estar vacia o tener menos de 6 caracteres");
        }
        if (!clave.equals(clave2)) {
            
            throw new ErrorServiceException("Las claves ingresadas deben coincidir");
        }
        if (usuarioRepository.buscarUsuarioPorNombre(nombre) != null) {
            throw new ErrorServiceException("Ya existe un usuario con el nombre ingresado");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarUsuarioPorNombre(nombre);
        if (usuario == null || usuario.getEliminado()) {
            throw new UsernameNotFoundException("Usuario no encontrado o eliminado");
        }
        List<GrantedAuthority> permisos = new ArrayList();
        GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
        permisos.add(p);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = requestAttributes.getRequest().getSession(true);
        session.setAttribute("usuariosession", usuario);
        return new User(usuario.getNombre(), usuario.getClave(), permisos);
    }

    public Usuario buscarUsuarioPorNombre(String nombre) throws ErrorServiceException{

        return usuarioRepository.buscarUsuarioPorNombre(nombre);
    }
    
}