package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.BaseEntity;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico utilizado por el controlador base del cliente.
 * Las implementaciones concretas pueden delegar en servicios remotos o capas locales.
 */
public interface BaseService<T extends BaseEntity> {

    T alta(T entidad) throws ErrorServiceException;

    Optional<T> modificar(Long id, T entidadNueva) throws ErrorServiceException;

    void baja(Long id) throws ErrorServiceException;

    Optional<T> obtener(Long id) throws ErrorServiceException;

    T obtenerEntidad(Long id) throws ErrorServiceException;

    List<T> listarActivos() throws ErrorServiceException;
}
