package com.is.biblioteca.business.logic.service.strategy;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.service.LibroService;
import com.is.biblioteca.business.persistence.repository.LibroRepository;

@Component
public class BusquedaPorAutor implements BusquedaStrategy {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> buscar(String valor) throws ErrorServiceException {
        return libroRepository.listarLibroPorAutor2(valor);
    }
}