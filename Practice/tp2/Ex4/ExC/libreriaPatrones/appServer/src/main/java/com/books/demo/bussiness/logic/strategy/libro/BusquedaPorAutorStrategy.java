package com.books.demo.bussiness.logic.strategy.libro;

import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.LibroRepository;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BusquedaPorAutorStrategy implements LibroBusquedaStrategy {

    private final LibroRepository libroRepository;

    public BusquedaPorAutorStrategy(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public LibroBusquedaTipo getTipo() {
        return LibroBusquedaTipo.AUTOR;
    }

    @Override
    public List<Libro> buscar(String criterio) throws ErrorServiceException {
        if (!StringUtils.hasText(criterio)) {
            throw new ErrorServiceException("Debe indicar un autor para realizar la búsqueda.");
        }
        String valor = criterio.trim();
        List<Libro> resultado = List.of();
        try {
            Long autorId = Long.parseLong(valor);
            resultado = libroRepository.buscarPorAutorId(autorId);
        } catch (NumberFormatException ignored) {
            // No es un número, se busca por nombre/apellido
        }
        if (resultado.isEmpty()) {
            resultado = libroRepository.buscarPorAutorNombre(valor);
        }
        return resultado;
    }
}
