package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.BaseEntity;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.BaseRepository;
import java.util.List;
import java.util.Optional;

/**
 * Servicio base con operaciones comunes de ABM y soporte de validaciones por caso de uso.
 */
public abstract class BaseService<T extends BaseEntity> {

    protected final BaseRepository<T> repository;

    protected BaseService(BaseRepository<T> repository) {
        this.repository = repository;
    }

    public T alta(T entidad) throws ErrorServiceException {
        try {
            entidad.setEliminado(false);
            validar(BaseUseCaseService.ALTA, entidad);
            preAlta(entidad);
            T guardado = repository.save(entidad);
            postAlta(guardado);
            return guardado;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al dar de alta la entidad.", e);
        }
    }

    public Optional<T> modificar(Long id, T entidadNueva) throws ErrorServiceException {
        try {
            entidadNueva.setId(id);
            validar(BaseUseCaseService.MODIFICACION, entidadNueva);
            preModificacion(entidadNueva);
            return repository.findById(id)
                    .filter(entidad -> !entidad.isEliminado())
                    .map(entidad -> repository.save(entidadNueva));
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al modificar la entidad.", e);
        }
    }

    public void baja(Long id) throws ErrorServiceException {
        try {
            T entidad = obtenerEntidad(id);
            validar(BaseUseCaseService.BAJA, entidad);
            preBaja(entidad);
            entidad.setEliminado(true);
            repository.save(entidad);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al eliminar la entidad.", e);
        }
    }

    public Optional<T> obtener(Long id) throws ErrorServiceException {
        try {
            return repository.findById(id)
                    .filter(entidad -> !entidad.isEliminado());
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al obtener la entidad.", e);
        }
    }

    public T obtenerEntidad(Long id) throws ErrorServiceException {
        return obtener(id)
                .orElseThrow(() -> new ErrorServiceException("Entidad no encontrada o eliminada."));
    }

    public List<T> listarActivos() throws ErrorServiceException {
        try {
            return repository.findAll().stream()
                    .filter(entidad -> !entidad.isEliminado())
                    .toList();
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al listar entidades.", e);
        }
    }

    protected void validar(BaseUseCaseService useCase, T entidad) throws ErrorServiceException {
    }

    protected void preAlta(T entidad) throws ErrorServiceException {
    }

    protected void postAlta(T entidad) throws ErrorServiceException {
    }

    protected void preModificacion(T entidad) throws ErrorServiceException {
    }

    protected void preBaja(T entidad) throws ErrorServiceException {
    }
}
