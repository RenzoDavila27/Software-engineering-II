package com.books.demo.bussiness.logic.strategy.libro;

import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.LibroRepository;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BusquedaPorGeneroStrategy implements LibroBusquedaStrategy {

    private final LibroRepository libroRepository;

    public BusquedaPorGeneroStrategy(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public LibroBusquedaTipo getTipo() {
        return LibroBusquedaTipo.GENERO;
    }

    @Override
    public List<Libro> buscar(String criterio) throws ErrorServiceException {
        if (!StringUtils.hasText(criterio)) {
            throw new ErrorServiceException("El género de búsqueda no puede estar vacío.");
        }
        return libroRepository.buscarPorGenero(criterio.trim());
    }
}
