package com.fioritech.demo.bussines.logic.service.template;

import java.util.Collection;

/**
 * Define la estructura general de las operaciones CRUD delegando los pasos
 * específicos a las subclases mediante el patrón Template Method.
 *
 * @param <T>  Tipo de la entidad de dominio.
 * @param <ID> Tipo del identificador primario.
 */
public abstract class CrudTemplateService<T, ID> {

    /**
     * Template Method para la creación de una entidad.
     */
    protected final T crearEntidad(T entidad) {
        validarEntidad(entidad);
        validarEntidadNueva(entidad);
        antesDeCrear(entidad);
        T guardada = guardar(entidad);
        despuesDeGuardar(guardada);
        return guardada;
    }

    /**
     * Template Method para la modificación de una entidad.
     */
    protected final T modificarEntidad(ID id, T cambios) {
        T existente = obtenerPorId(id);
        validarEntidad(cambios);
        antesDeModificar(existente, cambios);
        aplicarCambios(existente, cambios);
        T guardada = guardar(existente);
        despuesDeGuardar(guardada);
        return guardada;
    }

    /**
     * Template Method para la eliminación (lógica) de una entidad.
     */
    protected final void eliminarEntidad(ID id) {
        T existente = obtenerPorId(id);
        antesDeEliminar(existente);
        marcarEliminado(existente);
        guardar(existente);
        despuesDeEliminar(existente);
    }

    /**
     * Template Method para listar entidades activas.
     */
    protected final Collection<T> listarEntidades() {
        return obtenerListado();
    }

    /**
     * Template Method para buscar una entidad por ID.
     */
    protected final T buscarEntidad(ID id) {
        return obtenerPorId(id);
    }

    /* Hooks */

    protected abstract void validarEntidad(T entidad);

    protected abstract void validarEntidadNueva(T entidad);

    protected void antesDeCrear(T entidad) {
        // Hook opcional
    }

    protected void antesDeModificar(T existente, T cambios) {
        // Hook opcional
    }

    protected void antesDeEliminar(T entidad) {
        // Hook opcional
    }

    protected void despuesDeGuardar(T entidad) {
        // Hook opcional
    }

    protected void despuesDeEliminar(T entidad) {
        // Hook opcional
    }

    protected abstract void aplicarCambios(T existente, T cambios);

    protected abstract void marcarEliminado(T entidad);

    protected abstract T guardar(T entidad);

    protected abstract T obtenerPorId(ID id);

    protected abstract Collection<T> obtenerListado();
}

