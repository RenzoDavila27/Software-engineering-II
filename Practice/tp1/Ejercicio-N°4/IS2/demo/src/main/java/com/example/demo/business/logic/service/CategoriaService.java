package com.example.demo.business.logic.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.domain.Categoria;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.CategoriaRepository;
import java.util.Collection;
import java.util.Optional;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

    @Autowired
    private  CategoriaRepository repository;

    @Transactional
    public void crearCategoria(String nombre) throws ErrorServiceException{
        try{
            validar(nombre);
            try {
                Categoria catAux = repository.buscarCategoriaPorNombre(nombre);
                if (catAux != null && !catAux.isActivo()) {
                    throw new ErrorServiceException("Existe una categoria con el nombre indicado");
                }
            } catch (NoResultException ex) {}

            Categoria cat = new Categoria();
            cat.setNombre(nombre);
            cat.setActivo(true);
            repository.save(cat);

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
    public void editarCategoria(Long id, String nombre) throws ErrorServiceException {
        try{

            Categoria cat = buscarCategoria(id);
            if (nombre != null && !nombre.isBlank()) {
                cat.setNombre(nombre);
            }

            repository.save(cat);

        } catch (ErrorServiceException e){
            throw e; // la vuelvo a lanzar si quiero
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Categoria buscarCategoria(Long id) throws ErrorServiceException {

        try {

            if (id == null) {
                throw new ErrorServiceException("Debe indicar el país");
            }

            /* El método findById heredado de JpaRepository para buscar la entidad por su ID.
             * Este método devuelve un Optional<E>, que puede contener la entidad si se encuentra,
             * o estar vacío si no se encuentra.
             * ¿Qué es Optional?
             * Un Optional<E> es un contenedor que puede o no contener un valor no nulo de tipo E.
             * Los Optional se utilizan para evitar NullPointerException
             * y para expresar la ausencia de un valor de manera más clara.
             */

            Optional<Categoria> optional = repository.findById(id);
            Categoria cat = null;
            if (optional.isPresent()) {

                /*
                 * Si la entidad se encuentra, se obtiene de Optional usando get()
                 */
                cat = optional.get();

                /* Verifica si la entidad está marcada como eliminada lógicamente (entity.isEliminado()).
                 * Si es así, registra un mensaje de error y lanza una RuntimeException.
                 */
                if (!cat.isActivo()){
                    throw new ErrorServiceException("No se encuentra la categoria indicada");
                }
            }

            return cat;

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    @Transactional
    public void eliminarCategoria(Long id) throws ErrorServiceException {

        try {

            Categoria cat = buscarCategoria(id);
            cat.setActivo(false);

            repository.save(cat);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public Collection<Categoria> listarCategoria() throws ErrorServiceException {
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

    public Collection<Categoria> listarCategoriaActiva() throws ErrorServiceException {
        try {

            return repository.listarCategoriaActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }


}
