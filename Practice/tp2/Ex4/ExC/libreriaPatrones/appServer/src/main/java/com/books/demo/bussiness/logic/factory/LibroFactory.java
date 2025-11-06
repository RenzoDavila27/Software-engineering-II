package com.books.demo.bussiness.logic.factory;

import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.TipoLibro;
import org.springframework.stereotype.Component;

@Component
public class LibroFactory {

    public Libro.Builder configurarTipo(Libro.Builder builder, TipoLibro tipo) {
        if (builder == null) {
            throw new IllegalArgumentException("El builder de libro no puede ser nulo.");
        }
        TipoLibro tipoResuelto = tipo == null ? TipoLibro.FISICO : tipo;
        return switch (tipoResuelto) {
            case FISICO -> configurarFisico(builder);
            case DIGITAL -> configurarDigital(builder);
        };
    }

    private Libro.Builder configurarFisico(Libro.Builder builder) {
        return builder.tipo(TipoLibro.FISICO);
    }

    private Libro.Builder configurarDigital(Libro.Builder builder) {
        return builder.tipo(TipoLibro.DIGITAL);
    }
}
