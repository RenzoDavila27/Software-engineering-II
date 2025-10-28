package com.tinder.demo.bussines.logic.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tinder.demo.bussines.domain.Rol;
import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.persistence.repository.ZonaRepository;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.persistence.repository.UsuarioRepository;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService{

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private ZonaRepository repositoryZona;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        Usuario usuario = repository.buscarUsuarioPorMail(email);

        if (usuario != null){

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_"+ usuario.getRol());

            permisos.add(p);

            return new User(usuario.getMail(), usuario.getClave(), permisos);

        } else {
            return null;
        }
        
    }

    @Transactional
    public void crearUsuario(String nombre, String apellido, String mail, byte[] foto,String tipoFoto, String clave1,String clave2, Zona zona) throws ErrorServiceException{

        try{
            validar(nombre,apellido,mail,foto,zona,clave1,clave2);

            try{
                Usuario usuarioAux = repository.buscarUsuarioPorMail(mail);
                if (usuarioAux != null){
                    throw new ErrorServiceException("Existe un usuario con el mail indicado");
                }

            }catch(NoResultException e){}

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            usuario.setFoto(foto);
            usuario.setTipoFoto(tipoFoto);
            usuario.setClave(new BCryptPasswordEncoder().encode(clave1));
            usuario.setZona(zona);
            usuario.setFechadealta(new Date());
            usuario.setRol(Rol.USER);
            
            repository.save(usuario);

        }catch(ErrorServiceException e){
            throw e;

        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }
    

    public void validar(String nombre, String apellido, String mail, byte[] foto, Zona zona, String clave1, String clave2) throws ErrorServiceException{

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un nombre");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un apellido");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un mail");
        }
        if (foto.length == 0) {
            throw new ErrorServiceException("Debe cargar una foto");
        }
        if (zona == null) {
            throw new ErrorServiceException("Debe indicar una Zona");
        }
        if (clave1 == null || clave1.isEmpty()){
            throw new ErrorServiceException("Debe indicar una contraseña");
        }
        if (!clave1.equals(clave2)){
            throw new ErrorServiceException("Las contraseñas no coinciden");
        }

    }

    @Transactional
    public void modificarUsuario(Long id, String nombre, String apellido, String mail, byte[] foto,String tipoFoto, String claveActual, String clave1, String clave2,Zona zona) throws ErrorServiceException{

    try {
        Usuario usuario = null;
        boolean clave_modificada;
        usuario = repository.buscarUsuarioPorId(id);

        if (usuario == null){
            throw new ErrorServiceException("Ocurrio un error validar el usuario");
        }

        try {
            if (!usuario.getClave().equals(claveActual)){
                throw new ErrorServiceException("La contraseña actual no es correcta");
            }
            clave_modificada = modificaClave(clave1, clave2);
            validarModificacion(nombre,apellido,mail,foto,zona);
        } catch(ErrorServiceException e) {throw e;}

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setFoto(foto);
        usuario.setTipoFoto(tipoFoto);
        if (clave_modificada){
            usuario.setClave(clave1) ;
        }
        usuario.setZona(zona);

        repository.save(usuario);

    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }

    }

    public boolean modificaClave(String clave1, String clave2) throws ErrorServiceException{

        if (clave1 == null || clave1.isEmpty()) {
            if (!clave1.equals(clave2)) {
                throw new ErrorServiceException("Las contraseñas no coinciden");
            } else {
                return false;
            }
        }else {
            if (!clave1.equals(clave2)) {
                throw new ErrorServiceException("Las contraseñas no coinciden");
            }
        }
        return true;

    }

    public void validarModificacion(String nombre, String apellido, String mail, byte[] foto, Zona zona) throws ErrorServiceException{

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un nombre");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un apellido");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un mail");
        }
        if (foto.length == 0) {
            throw new ErrorServiceException("Debe cargar una foto");
        }
        if (zona == null) {
            throw new ErrorServiceException("Debe indicar una Zona");
        }

    }

    @Transactional
    public void eliminarUsuario(Long id) throws ErrorServiceException{

        try{
            Usuario usuario = null;
            try{
                usuario = repository.buscarUsuarioPorId(id);


            }catch(NoResultException e){
                throw new ErrorServiceException("No se ha encontrado el Usuario");
            }
            usuario.setFechadebaja(new Date());

        }catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Usuario verificarUsuario(String mail, String clave) throws ErrorServiceException{

        try{

            validarLogin(mail, clave);
            Usuario usuario = repository.buscarUsuarioPorMail(mail);

            if (usuario == null){
                throw new ErrorServiceException("El mail no esta registrado");
            }
            if (usuario.getClave().equals(clave)){
                return usuario;
            }else{
                throw new ErrorServiceException("La contraseña no corresponde al usuario");
            }
        }catch(Exception e){
            throw e;
        }


    }

    public void validarLogin(String mail, String clave) throws ErrorServiceException{

        if (mail == null || mail.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un mail");
        }
        if (clave == null || clave.isEmpty()) {
            throw new ErrorServiceException("Debe indicar una contraseña");
        }

    }

    public Usuario buscarUsuarioPorMail(String mail) throws ErrorServiceException {
        try {
            if (mail == null || mail.isEmpty()) {
                throw new ErrorServiceException("Debe indicar un mail");
            }
            return repository.buscarUsuarioPorMail(mail);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }


    public Collection<Usuario> listarUsuarios() throws ErrorServiceException{
        try{
            Collection<Usuario> usuarios = null;
            try{
                usuarios = repository.buscarUsuarios();

            }catch (NoResultException e){
                throw new ErrorServiceException("No se encontraron Usuarios en la base de datos");
            }

            return usuarios;


        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistemas");
        }
    }

    public Collection<Usuario> listarUsuariosActivos() throws ErrorServiceException{

        try{
            Collection<Usuario> usuarios = null;
            try{
                usuarios = repository.buscarUsuariosActivos();

            }catch (NoResultException e){
                throw new ErrorServiceException("No se encontraron Usuarios activos en la base de datos");
            }

            return usuarios;


        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistemas");
        }

    }

    public Usuario buscarUsuarioPorId(Long id) throws ErrorServiceException{

        try{
            Usuario usuario = null;
            try{
                usuario = repository.buscarUsuarioPorId(id);

            }catch (NoResultException e){
                throw new ErrorServiceException("No se encontro el usuario");
            }

            return usuario;


        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistemas");
        }

    }

}
