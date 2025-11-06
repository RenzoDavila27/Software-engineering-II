package com.example.demo.business.logic.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.business.domain.ClaseBase;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.BaseRepository;

public abstract class BaseService<T extends ClaseBase> {

    protected final BaseRepository<T> repository;

    protected BaseService(BaseRepository<T> repository) {
        this.repository = repository;
    }

    @Transactional
    public T crear(T entidad) throws ErrorServiceException {
        try {
            entidad.setId(null);
            validar(entidad);
            preCrear(entidad);
            entidad.setEliminado(false);
            T guardado = repository.save(entidad);
            postCrear(guardado);
            return guardado;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public T actualizar(Long id, T entidad) throws ErrorServiceException {
        try {
            T existente = obtenerActivo(id);
            preActualizar(existente, entidad);
            copiarPropiedades(entidad, existente);
            validar(existente);
            T actualizado = repository.save(existente);
            postActualizar(actualizado);
            return actualizado;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public void eliminar(Long id) throws ErrorServiceException {
        try {
            T existente = obtenerActivo(id);
            preEliminar(existente);
            existente.setEliminado(true);
            repository.save(existente);
            postEliminar(existente);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public List<T> listarActivos() throws ErrorServiceException {
        try {
            return repository.findAll().stream()
                    .filter(entidad -> !Boolean.TRUE.equals(entidad.getEliminado()))
                    .toList();
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public T obtenerActivo(Long id) throws ErrorServiceException {
        try {
            return repository.findById(id)
                    .filter(entidad -> !Boolean.TRUE.equals(entidad.getEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("No se encuentra el registro solicitado"));
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    protected void validar(T entidad) throws ErrorServiceException {}

    protected void preCrear(T entidad) throws ErrorServiceException {}

    protected void postCrear(T entidad) throws ErrorServiceException {}

    protected void preActualizar(T existente, T datos) throws ErrorServiceException {}

    protected void postActualizar(T entidad) throws ErrorServiceException {}

    protected void preEliminar(T entidad) throws ErrorServiceException {}

    protected void postEliminar(T entidad) throws ErrorServiceException {}

    protected abstract void copiarPropiedades(T origen, T destino) throws ErrorServiceException;
}
