package com.is.biblioteca.business.logic.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;
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

import com.is.biblioteca.business.domain.entity.Imagen;
import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.domain.enumeration.Rol;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.persistence.repository.UsuarioRepository;

import jakarta.persistence.NoResultException;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
    private UsuarioRepository repository;

	@Autowired
    private ImagenService imagenService;
	
    private void validarDatosBasicos(String nombre, String email) throws ErrorServiceException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el Email");
        }
    }

    private void validarClave(String clave, String confirmacion) throws ErrorServiceException {
        if (clave == null || clave.trim().isEmpty() || clave.length() < 6) {
            throw new ErrorServiceException("La contraseña debe tener al menos 6 caracteres");
        }

        if (confirmacion == null || confirmacion.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar la confirmación de clave");
        }

        if (!clave.trim().equals(confirmacion.trim())) {
            throw new ErrorServiceException("La clave debe ser igual a su confirmación");
        }
    }
    
    @Transactional
    public Usuario crearUsuario(String nombre, String email, String clave, String confirmacion, MultipartFile archivo) throws ErrorServiceException {

      try {	
    	  
        validarDatosBasicos(nombre, email);
        validarClave(clave, confirmacion);
        
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID().toString());
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(Rol.USER);
        usuario.setPassword(new BCryptPasswordEncoder().encode(clave));
        usuario.setEliminado(false);
 
        if (archivo != null) {        
         Imagen imagen = imagenService.crearImagen(archivo);
         usuario.setImagen(imagen);
        } 
        
        return repository.save(usuario);
        
      }catch(ErrorServiceException e) {  
		   throw e;  
	  }catch(Exception e) {
		   e.printStackTrace();
		   throw new ErrorServiceException("Error de Sistemas");
	  } 
    }

    @Transactional
    public Usuario modificarUsuario(String idUsuario, String nombre, String email, String clave, String confirmacion, MultipartFile archivo) throws ErrorServiceException {

    	try {
    		
    		validarDatosBasicos(nombre, email);
    		
            Usuario usuario = buscarUsuario(idUsuario);
            usuario.setNombre(nombre);
            usuario.setEmail(email);

            boolean claveIngresada = clave != null && !clave.trim().isEmpty();
            boolean confirmacionIngresada = confirmacion != null && !confirmacion.trim().isEmpty();

            if (claveIngresada || confirmacionIngresada) {
                validarClave(clave, confirmacion);
                usuario.setPassword(new BCryptPasswordEncoder().encode(clave));
            }

            String idImagen = null;
            if (usuario.getImagen() != null) {
            	idImagen = usuario.getImagen().getId();
            }
            
            if (archivo != null && !archivo.isEmpty()) {
                Imagen imagen;
                if (idImagen != null) {
                    imagen = imagenService.modificarImagen(idImagen, archivo);
                } else {
                    imagen = imagenService.crearImagen(archivo);
                }
                usuario.setImagen(imagen);
            }
            
            return repository.save(usuario);
            
    	}catch(ErrorServiceException e) {  
  		   throw e;  
  	  	}catch(Exception e) {
  		   e.printStackTrace();
  		   throw new ErrorServiceException("Error de Sistemas");
  	  	} 
    }
    
    @Transactional
    public void eliminarUsuario(String idUsuario) throws ErrorServiceException {

    	 try {		

            Usuario usuario = buscarUsuario(idUsuario);
            usuario.setEliminado(true);

            repository.save(usuario);
            
          }catch(ErrorServiceException e) {  
    		   throw e;  
    	  }catch(Exception e) {
    		   e.printStackTrace();
    		   throw new ErrorServiceException("Error de Sistemas");
    	  } 

    }
    
    @Transactional
    public void cambiarRol(String idUsuario) throws ErrorServiceException {

    	 try {		

            Usuario usuario = buscarUsuario(idUsuario);
            
            if(usuario.getRol() == Rol.ADMIN)
              usuario.setRol(Rol.USER);
            else
              usuario.setRol(Rol.ADMIN);

            repository.save(usuario);
            
          }catch(ErrorServiceException e) {  
    		   throw e;  
    	  }catch(Exception e) {
    		   e.printStackTrace();
    		   throw new ErrorServiceException("Error de Sistemas");
    	  } 

    }
    
    @Transactional(readOnly=true)
    public Usuario buscarUsuario(String idUsuario) throws ErrorServiceException {

    	try {
            
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el usuario");
            }

            Optional<Usuario> optional = repository.findById(idUsuario);
            Usuario usuario = null;
            if (optional.isPresent()) {
            	usuario= optional.get();
    			if (usuario == null || usuario.isEliminado()){
                    throw new ErrorServiceException("No se encuentra el usuario indicado");
                }
    		}
            
            return usuario;
            
        } catch (ErrorServiceException ex) {  
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }
    
    public Usuario buscarUsuarioPorEmail (String email) throws ErrorServiceException {
    	
    	try {	
    		
    		if (email == null || email.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el email");
            }
    		
    		return repository.buscarUsuarioPorEmail(email);
        
    	 } catch (ErrorServiceException ex) {  
             throw ex;
         } catch (Exception ex) {
             ex.printStackTrace();
             throw new ErrorServiceException("Error de sistema");
         }
    }

    public Usuario buscarUsuarioPorNombre (String nombre) throws ErrorServiceException {
    	
    	try {	
    		
    		if (nombre == null || nombre.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
    		
    		return repository.buscarUsuarioPorNombre(nombre);
        
    	 } catch (ErrorServiceException ex) {  
             throw ex;
         } catch (Exception ex) {
             ex.printStackTrace();
             throw new ErrorServiceException("Error de sistema");
         }
    }

    public List<Usuario> listarUsuario()throws ErrorServiceException {
 
	  try { 
		
        return repository.findAll();

      }catch(Exception e) {
   	   e.printStackTrace();
   	   throw new ErrorServiceException("Error de Sistemas");
      }
        
    }
    

    public Usuario login(String email, String clave) throws ErrorServiceException {
    	
    	try {
    		
    		if (email == null || email.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el usuario");
            }

            if (clave == null || clave.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar la clave");
            }
            
            Usuario usuario = null; 
            try {		
             usuario = repository.buscarUsuarioPorEmailYClave(email, clave);
            } catch (NoResultException ex) {
            	throw new ErrorServiceException("No existe usuario para el correo y clave indicado");
            }
    		
            return usuario;
            
    	}catch(ErrorServiceException e) {  
         	throw e;  
        }catch(Exception e) {
         	e.printStackTrace();
         	throw new ErrorServiceException("Error de Sistemas");
        } 
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = null;
        try {
            usuario = buscarUsuarioPorEmail(email);
        } catch (ErrorServiceException e) {
            throw new RuntimeException(e);
        }
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = requestAttributes.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        }else {
            return null;
        }
    }
}
