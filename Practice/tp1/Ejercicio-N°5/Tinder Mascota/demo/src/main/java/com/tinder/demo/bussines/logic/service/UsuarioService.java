package com.tinder.demo.bussines.logic.service;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.persistence.repository.UsuarioRepository;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Transactional
    public void crearUsuario(String nombre, String apellido, String mail, byte[] foto, String clave, Zona zona) throws ErrorServiceException{

        try{
            validar(nombre,apellido,mail,foto,zona);

            try{
                Usuario usuarioAux = repository.buscarUsuarioPorMail(mail);
                if (usuarioAux != null){
                    throw new ErrorServiceException("Existe un Usuario con el mail indicado");
                }

            }catch(NoResultException e){}

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            usuario.setFoto(foto);
            usuario.setClave(clave);
            usuario.setZona(zona);
            usuario.setFechadealta(new Date());
            
            repository.save(usuario);

        }catch(ErrorServiceException e){
            throw e;

        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }
    

    public void validar(String nombre, String apellido, String mail, byte[] foto, Zona zona) throws ErrorServiceException{

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un nombre");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un apellido");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un mail");
        }
        if (foto == null) {
            throw new ErrorServiceException("Debe cargar una foto");
        }
        if (zona == null || nombre.isEmpty()) {
            throw new ErrorServiceException("Debe indicar una Zona");
        }

    }

    @Transactional
    public void modificarUsuario(Long id, String nombre, String apellido, String mail, byte[] foto, String clave,Zona zona) throws ErrorServiceException{

    try {
        Usuario usuario = null;

            try {
                usuario = repository.buscarUsuarioPorId(id);
            } catch (NoResultException e) {
                throw new ErrorServiceException("No se encontró el usuario con id " + id);
            }

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setFoto(foto);
        usuario.setClave(clave);
        usuario.setZona(zona);

        repository.save(usuario);

    } catch (Exception e) {
        e.printStackTrace();
        throw new ErrorServiceException("Error de Sistemas");
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
            Usuario usuario = null;
            try{
                usuario = repository.buscarUsuarioPorMail(mail);
                if (usuario.getClave().equals(clave)){
                    return usuario;
                }else{
                    throw new ErrorServiceException("Su contraseña o mail son equivocadas");
                }
            }catch(NoResultException e){
                throw new ErrorServiceException("Su contraseña o mail son equivocadas");
            }

        }catch(Exception ex){
            ex.printStackTrace();
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
