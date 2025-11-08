package com.car.business.logic.service;

import java.util.List;
import java.util.Optional;

import com.car.business.domain.BaseEntity;
import com.car.business.percistence.repository.BaseRepository;
import com.car.business.logic.error.BusinessException;

public abstract class BaseService<T extends BaseEntity<ID>, ID> {

    protected final BaseRepository<T, ID> repository;

    protected BaseService(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T alta(T entidad) throws BusinessException {
      try {		
    	  
    	validar(entidad);
        preAlta(entidad);
        
        entidad.setEliminado(false);
        T guardado = repository.save(entidad);
        
        postAlta(guardado);
        return guardado;
        
      }catch(Exception e) {
      	throw new BusinessException("Error de Sistemas");  
      }   
    }

    public Optional<T> modificar(ID id, T entidadNueva)throws BusinessException {
      try {	
    	  
    	validar(entidadNueva);
        preModificacion(entidadNueva);
        return repository.findById(id).map(entidad -> {
        	entidadNueva.setId(id);
            T actualizado = repository.save(entidadNueva);
            return actualizado;
        });
        
      }catch(Exception e) {
    	throw new BusinessException("Error de Sistemas");  
      }   
    }

    public boolean bajaLogica(ID id)throws BusinessException {
      try {		
    	  
    	preBaja(id);
    	
        return repository.findById(id).map(entidad -> {
            entidad.setEliminado(true);
            repository.save(entidad);
            return true;
        }).orElse(false);
        
      }catch(Exception e) {
  		throw new BusinessException("Error de Sistemas");  
  	  }  
    }

    public Optional<T> obtener(ID id)throws BusinessException {
      try {	 	
    	  
        return repository.findById(id).
        	              filter(e -> !Boolean.TRUE.equals(e.getEliminado()));
        
      }catch(Exception e) {
    	throw new BusinessException("Error de Sistemas");  
      } 
    }

    public List<T> listarActivos()throws BusinessException {
      try {	
    	  
        return repository.findAll().stream()
                         .filter(e -> !Boolean.TRUE.equals(e.getEliminado()))
                         .toList(); 
        
	  }catch(Exception e) {
		throw new BusinessException("Error de Sistemas");  
	  }  
    }

    //Metodos para ser redefinidos en las clases de servicio que heredan, con el objetivo
    //que sea necesario realizar acciones previas o posteriores en las Altas, Bajas y
    //Modificaciones.
    //Se deberá redefinir el comportamiento en la clase que hereda.
    protected void validar(T entidad) throws BusinessException {}
    protected void preAlta(T entidad) throws BusinessException {}
    protected void postAlta(T entidad)throws BusinessException {}
    protected void preModificacion(T entidad)throws BusinessException {}
    protected void preBaja(ID id)throws BusinessException {}
}