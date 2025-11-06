package com.books.demo.bussiness.logic.iterator;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Libro;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Implementa el patrón Iterador filtrando los libros que pertenecen a un autor determinado.
 */
public class LibroPorAutorIterator implements LibroIterator {

    private final Iterator<Libro> origen;
    private final Long autorId;
    private Libro siguiente;

    public LibroPorAutorIterator(Collection<Libro> libros, Long autorId) {
        this.origen = (libros == null ? Collections.<Libro>emptyList() : libros).iterator();
        this.autorId = autorId;
        avanzar();
    }

    @Override
    public boolean hasNext() {
        return siguiente != null;
    }

    @Override
    public Libro next() {
        if (siguiente == null) {
            throw new NoSuchElementException("No hay más libros que coincidan con el autor indicado.");
        }
        Libro actual = siguiente;
        avanzar();
        return actual;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("La operación remove no está soportada en este iterador.");
    }

    private void avanzar() {
        siguiente = null;
        while (origen.hasNext()) {
            Libro candidato = origen.next();
            if (perteneceAlAutor(candidato, autorId)) {
                siguiente = candidato;
                break;
            }
        }
    }

    private boolean perteneceAlAutor(Libro libro, Long autorId) {
        if (libro == null || autorId == null) {
            return false;
        }
        Collection<Autor> autores = libro.getAutores();
        if (autores == null || autores.isEmpty()) {
            return false;
        }
        return autores.stream()
                .filter(Objects::nonNull)
                .map(Autor::getId)
                .anyMatch(id -> Objects.equals(id, autorId));
    }
}
