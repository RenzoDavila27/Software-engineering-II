package com.tinder.demo.bussines.logic.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.persistence.repository.ZonaRepository;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class ZonaService {

    @Autowired
    private ZonaRepository repository;

    @Transactional
    public void crearZona(String nombre) throws ErrorServiceException{

        try{
            Zona zona = new Zona();
            try{
                zona = repository.buscarZonaPorNombre(nombre);
                if (zona!=null){
                    throw new ErrorServiceException("Ya existe una Zona con ese nombre");
                }

            }catch(NoResultException e){}
            zona.setNombre(nombre);
            repository.save(zona);
        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

        
    }

    @Transactional
    public void modificarZona(Long id,String nombre) throws ErrorServiceException{

        try{
            Optional<Zona> optional = repository.findById(id);
            
            if (optional.isPresent()){
                Zona zona = optional.get();
                zona.setNombre(nombre);
                repository.save(zona);
            }else{
                throw new ErrorServiceException("No se encontro ninguna Zona");
            }



        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
    
    @Transactional
    public void eliminarZona(Long id) throws ErrorServiceException{

        try{
            Optional<Zona> optional = repository.findById(id);
            
            if (optional.isPresent()){
                Zona zona = optional.get();
                zona.setEliminado(true);
                repository.save(zona);
            }else{
                throw new ErrorServiceException("No se encontro ninguna Zona");
            }

        }catch(Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }

    public Collection<Zona> buscarZonasActivas() throws ErrorServiceException{

        try{
            Collection<Zona> zonas = repository.listarZonasActivas();
            return zonas;
        }catch(Exception e){
            e.printStackTrace();
            throw new ErrorServiceException("Error del Sistema");
        }
    }


}
