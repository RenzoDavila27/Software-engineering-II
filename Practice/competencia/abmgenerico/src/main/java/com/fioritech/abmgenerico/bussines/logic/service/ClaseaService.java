package com.example.abmgenerico.business.logic.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.abmgenerico.business.domain.Clasea;
import com.example.abmgenerico.business.logic.error.ErrorServiceException;
import com.example.abmgenerico.business.persistence.repository.ClaseaRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class ClaseaService {
    
    @Autowired
    private  ClaseaRepository repository;

    @Transactional
    public void crearClasea(String str1, String rutaimg, float float1, Short short1, String str2,
     Boolean bool1,String str3) throws ErrorServiceException{
        try{
            validar(str1);
            try {
            	Clasea ClaseaAux = repository.buscarClaseaPorStr1(str1);
            	if (ClaseaAux != null && !ClaseaAux.getActivo()) {
                 throw new ErrorServiceException("Existe un Clasea con el str1 indicado");
            	} 
            } catch (NoResultException ex) {}

            Clasea clasea = new Clasea();
            clasea.setStr1(str1);
            clasea.setRutaimg(rutaimg);
            clasea.setFloat1(float1);
            clasea.setShort1(short1);
            clasea.setStr2(str2);
            clasea.setBool1(bool1);
            clasea.setStr3(str3);
            clasea.setActivo(true);
            repository.save(clasea);
            


        } catch (ErrorServiceException e){
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public void validar(String str1)throws ErrorServiceException {
        
        try{
            
            if (str1 == null || str1.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el str1");
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public void editarClasea(Long id,String str1, String rutaimg, float float1, Short short1, String str2,
        Boolean bool1,String str3) throws ErrorServiceException {
        try{
            
            Clasea clasea = buscarClasea(id);
            if (str1 != null && !str1.isBlank()) {
                clasea.setStr1(str1);
            }
            if (rutaimg != null && !rutaimg.isBlank()) {
                clasea.setRutaimg(rutaimg);
            }
            if (float1 > 0) {
                clasea.setFloat1(float1);
            }
            if (short1 != null) {
                clasea.setShort1(short1);
            }
            if (str2 != null && !str2.isBlank()) {
                clasea.setStr2(str2);
            }
            if (bool1 != null) {
                clasea.setBool1(bool1);
            }
            if (str3 != null && !str3.isBlank()) {
                clasea.setStr3(str3);
            }
            repository.save(clasea);

        } catch (ErrorServiceException e){
            throw e; // la vuelvo a lanzar si quiero
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Clasea buscarClasea(Long id) throws ErrorServiceException {
        try {
            if (id == null) {
                throw new ErrorServiceException("Debe indicar la categoría");
            }

            var optional = repository.findById(id);
            if (optional.isEmpty() || !optional.get().getActivo()) {
                throw new ErrorServiceException("No se encuentra la categoría indicada");
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
    public Collection<Clasea> buscarClaseaPorStr1(String str1) throws ErrorServiceException {
        try {
            if (str1 == null || str1.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el str1");
            }

            Collection<Clasea> listaClasea = List.of(repository.buscarClaseaPorStr1(str1));
            return listaClasea;

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }


    @Transactional
    public void eliminarClasea(Long id) throws ErrorServiceException {  

        try {

            Clasea clasea = buscarClasea(id);
            clasea.setActivo(false);
            
            repository.save(clasea);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public Collection<Clasea> listarClasea() throws ErrorServiceException {
        try {
            
        	/* findAll(): Este método de JpaRepository
             * se utiliza para obtener todas las entidades del tipo E desde la base de datos.
             * Devuelve una lista (List<E>) de todas las entidades.
             */
        	
            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    public Collection<Clasea> listarClaseaActivo() throws ErrorServiceException {
        try {
            
            return repository.listarClaseaActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }


}
