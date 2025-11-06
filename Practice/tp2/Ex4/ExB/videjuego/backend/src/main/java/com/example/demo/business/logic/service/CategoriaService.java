package com.example.demo.business.logic.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.business.domain.Categoria;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.CategoriaRepository;

@Service
public class CategoriaService extends BaseService<Categoria> {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public Collection<Categoria> listarActivasDesdeConsulta() throws ErrorServiceException {
        try {
            return repository.listarCategoriaActivo();
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Collection<Categoria> buscarPorNombre(String nombre) throws ErrorServiceException {
        try {
            if (nombre == null || nombre.isBlank()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            Categoria categoria = repository.buscarCategoriaPorNombre(nombre);
            return categoria == null ? List.of() : List.of(categoria);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Override
    protected void validar(Categoria entidad) throws ErrorServiceException {
        if (entidad.getNombre() == null || entidad.getNombre().isBlank()) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }
    }

    @Override
    protected void preCrear(Categoria entidad) throws ErrorServiceException {
        Categoria existente = repository.buscarCategoriaPorNombre(entidad.getNombre());
        if (existente != null && !Boolean.TRUE.equals(existente.getEliminado())) {
            throw new ErrorServiceException("Existe una categoria con el nombre indicado");
        }
    }

    @Override
    protected void copiarPropiedades(Categoria origen, Categoria destino) throws ErrorServiceException {
        destino.setNombre(origen.getNombre());
    }
}
