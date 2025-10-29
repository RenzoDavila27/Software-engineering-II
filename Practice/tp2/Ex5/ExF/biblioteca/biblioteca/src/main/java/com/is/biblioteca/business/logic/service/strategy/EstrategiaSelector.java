package com.is.biblioteca.business.logic.service.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EstrategiaSelector {

    @Autowired
    private BusquedaPorTitulo buscarPorTitulo;
    @Autowired
    private BusquedaPorAutor buscarPorAutor;
    @Autowired
    private BusquedaPorEditorial buscarPorEditorial;

    public BusquedaStrategy getEstrategia(String tipo) {
        switch (tipo.toLowerCase()) {
            case "titulo":
                return buscarPorTitulo;
            case "autor":
                return buscarPorAutor;
            case "editorial":
                return buscarPorEditorial;
            default:
                throw new IllegalArgumentException("Tipo de búsqueda no válido: " + tipo);
        }
    }
}
