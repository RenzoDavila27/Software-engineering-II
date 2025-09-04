package com.tinder.demo.bussines.logic.service;

import com.tinder.demo.bussines.domain.*;
import com.tinder.demo.bussines.persistence.repository.MascotaRepository;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.persistence.repository.UsuarioRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tinder.demo.bussines.persistence.repository.MascotaRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Date;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository repositoryMascota;

    @Autowired
    private UsuarioRepository repositoryUsuario;

    @Transactional
    public void crearMascota(String nombre,Sexo sexo, Tipo tipo, byte[] foto,String fotoTipo, Long idUsuario) throws ErrorServiceException {

        try{

            validar(nombre,foto,idUsuario);

            Mascota mascota = new Mascota();
            mascota.setUsuario(repositoryUsuario.buscarUsuarioPorId(idUsuario));
            mascota.setNombre(nombre);
            mascota.setSexo(sexo);
            mascota.setFoto(foto);
            mascota.setFotoTipo(fotoTipo);
            mascota.setTipo(tipo);
            mascota.setFechadealta(new Date());

            repositoryMascota.save(mascota);

        }catch(ErrorServiceException e){
            throw e;

        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public void validar(String nombre, byte[] foto, Long idUsuario) throws ErrorServiceException{

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServiceException("Debe indicar un nombre");
        }
        if (foto == null) {
            throw new ErrorServiceException("Debe cargar una foto");
        }
        if (idUsuario== null || repositoryUsuario.buscarUsuarioPorId(idUsuario) == null) {
            throw new ErrorServiceException("No se ha iniciado sesion");
        }

    }

    @Transactional
    public void modificarMascota(Long id, String nombre, Tipo tipo,Sexo sexo, byte[] archivo, String fotoTipo) throws ErrorServiceException{

        try {
            Mascota mascota;

            mascota = repositoryMascota.buscarMascotaPorId(id);

            if (mascota == null){
                throw new ErrorServiceException("No se encontro la mascota");
            }

            mascota.setNombre(nombre);
            mascota.setSexo(sexo);
            mascota.setTipo(tipo);
            mascota.setFoto(archivo);
            mascota.setFotoTipo(fotoTipo);

            repositoryMascota.save(mascota);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }

    @Transactional
    public void darDeBajaMascota(Long id) throws ErrorServiceException{

        try{

            Mascota mascota = repositoryMascota.buscarMascotaPorId(id);

            if(mascota != null){
                mascota.setFechadebaja(new Date());
                repositoryMascota.save(mascota);
            }else{
                throw new ErrorServiceException("No se encontro la mascota");
            }

        }catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public void darDeAltaMascota(Long id) throws ErrorServiceException{

        try{

            Mascota mascota = repositoryMascota.buscarMascotaPorId(id);

            if(mascota != null){
                mascota.setFechadebaja(null);
                repositoryMascota.save(mascota);
            }else{
                throw new ErrorServiceException("No se encontro la mascota");
            }

        }catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Collection<Mascota> listarMascotasActivas() throws ErrorServiceException{

        Collection<Mascota> mascotas = null;
        mascotas = repositoryMascota.buscarMascotasActivas();
        return mascotas;

    }

    public Collection<Mascota> listarMascotasInactivas(Long id) throws ErrorServiceException{

        Collection<Mascota> mascotas = null;
        mascotas = repositoryMascota.buscarMascotasPorUsuarioInactivas(id);
        return mascotas;

    }

    public Mascota buscarMascotaPorId(Long id) throws ErrorServiceException{

        try{
            Mascota mascota = repositoryMascota.buscarMascotaPorId(id);

            if (mascota == null){
                throw new ErrorServiceException("No se encontro la mascota");
            }

            return mascota;


        }catch (ErrorServiceException e){
            throw e;
        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistemas");
        }
    }

    public Collection<Mascota> listarMascotasPorUsuario(Long id) throws ErrorServiceException{

        Collection<Mascota> mascotas = null;
        mascotas = repositoryMascota.buscarMascotasPorUsuarioActivas(id);
        return mascotas;

    }
}
