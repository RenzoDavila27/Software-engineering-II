package com.is.biblioteca.business.logic.service.strategy;

import java.util.List;
import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;

public interface BusquedaStrategy {
    List<Libro> buscar(String criterio) throws ErrorServiceException;
}