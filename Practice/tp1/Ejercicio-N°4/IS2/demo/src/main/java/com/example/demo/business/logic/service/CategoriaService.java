package com.example.demo.business.logic.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.domain.Categoria;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.CategoriaRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

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
            throw new ErrorServiceException("Debe indicar la categoría");
        }

        var optional = repository.findById(id);
        if (optional.isEmpty() || !optional.get().isActivo()) {
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
public Collection<Categoria> buscarCategoriaPorNombre(String nombre) throws ErrorServiceException {
    try {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }

        Collection<Categoria> listaCategoria = List.of(repository.buscarCategoriaPorNombre(nombre));
        return listaCategoria;

    } catch (ErrorServiceException e) {
        throw e;
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
