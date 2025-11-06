package com.books.demo.bussiness.logic.strategy.libro;

import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import java.util.List;

public interface LibroBusquedaStrategy {

    LibroBusquedaTipo getTipo();

    List<Libro> buscar(String criterio) throws ErrorServiceException;
}
