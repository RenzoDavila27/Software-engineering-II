package com.example.demo.business.logic.service;

import com.example.demo.business.domain.Estudio;
import com.example.demo.business.logic.error.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.persistence.repository.EstudioRepository;
import jakarta.persistence.NoResultException;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;


@Service
public class EstudioService {
    
    @Autowired
    private EstudioRepository repository;
    
    @Transactional
    public void crearEstudio(String nombre, Boolean activo) throws ErrorServiceException{
        try{
            validar(nombre);
            try {
            	Estudio EstudioAux = repository.buscarEstudioPorNombre(nombre);
            	if (EstudioAux != null && !EstudioAux.getActivo()) {
                 throw new ErrorServiceException("Existe un estudio con el nombre indicado");
            	} 
            } catch (NoResultException ex) {}

            Estudio estudio = new Estudio();
            estudio.setNombre(nombre);
            estudio.setActivo(true);
            repository.save(estudio);
            
            
            
        } catch (ErrorServiceException e){
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
    
    public void validar(String nombre)throws ErrorServiceException {
        
        try{
            
            if (nombre == null || nombre.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
    
    @Transactional
    public void editarEstudio(Long id,String nombre) throws ErrorServiceException {
        try{
            
            Estudio estudio = buscarEstudio(id);
            if (nombre != null && !nombre.isBlank()) {
                estudio.setNombre(nombre);
            }
            
            repository.save(estudio);

        } catch (ErrorServiceException e){
            throw e; // la vuelvo a lanzar si quiero
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
    
public Estudio buscarEstudio(Long id) throws ErrorServiceException {
    try {
        if (id == null) {
            throw new ErrorServiceException("Debe indicar el estudio");
        }

        var optional = repository.findById(id);
        if (optional.isEmpty() || !optional.get().isActivo()) {
            throw new ErrorServiceException("No se encuentra el estudio indicado");
        }

        return optional.get();

    } catch (ErrorServiceException e) {
        throw e;
    } catch (Exception ex) {
        ex.printStackTrace();
        throw new ErrorServiceException("Error de sistema");
    }
}
    
    @Transactional
    public void eliminarEstudio(Long id) throws ErrorServiceException {  

        try {

            Estudio estudio = buscarEstudio(id);
            estudio.setActivo(false);
            
            repository.save(estudio);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public Collection<Estudio> listarEstudio() throws ErrorServiceException {
        try {

            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    public Collection<Estudio> listarEstudioActivo() throws ErrorServiceException {
        try {
            
            return repository.listarEstudioActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }
}
