package com.fioritech.demo.bussines.controller.template;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;

/**
 * Define el flujo común de los controladores basados en formularios CRUD
 * aplicando el patrón Template Method. Las subclases únicamente completan los
 * pasos variables.
 *
 * @param <T>  Tipo de entidad manejada por el controlador.
 * @param <ID> Tipo del identificador primario.
 */
public abstract class CrudTemplateController<T, ID> {

    @GetMapping("/listar")
    public final String listar(Model model) {
        model.addAttribute(obtenerNombreModeloListado(), listarEntidades());
        prepararModeloListado(model);
        return obtenerVistaListado();
    }

    @GetMapping("/crear")
    public final String mostrarFormularioCreacion(Model model) {
        T entidad = crearInstanciaFormulario();
        prepararInstanciaNueva(entidad);
        model.addAttribute(obtenerNombreModeloFormulario(), entidad);
        prepararModeloFormulario(model);
        return obtenerVistaCreacion();
    }

    @PostMapping("/crear")
    public final String crear(@ModelAttribute T entidad) {
        crearEntidad(entidad);
        return obtenerRedirectListado();
    }

    @GetMapping("/modificar/{id}")
    public final String mostrarFormularioEdicion(@PathVariable ID id, Model model) {
        T entidad = buscarEntidad(id);
        prepararInstanciaExistente(entidad);
        model.addAttribute(obtenerNombreModeloFormulario(), entidad);
        prepararModeloFormulario(model);
        return obtenerVistaEdicion();
    }

    @PostMapping("/modificar/{id}")
    public final String modificar(@PathVariable ID id, @ModelAttribute T cambios) {
        modificarEntidad(id, cambios);
        return obtenerRedirectListado();
    }

    @GetMapping("/eliminar/{id}")
    public final String eliminar(@PathVariable ID id) {
        eliminarEntidad(id);
        return obtenerRedirectListado();
    }

    /* Hooks */

    protected void prepararModeloListado(Model model) {
        // Hook opcional
    }

    protected void prepararModeloFormulario(Model model) {
        // Hook opcional
    }

    protected void prepararInstanciaNueva(T entidad) {
        // Hook opcional
    }

    protected void prepararInstanciaExistente(T entidad) {
        // Hook opcional
    }

    protected abstract Collection<T> listarEntidades();

    protected abstract void crearEntidad(T entidad);

    protected abstract T buscarEntidad(ID id);

    protected abstract void modificarEntidad(ID id, T cambios);

    protected abstract void eliminarEntidad(ID id);

    protected abstract T crearInstanciaFormulario();

    protected abstract String obtenerNombreModeloListado();

    protected abstract String obtenerNombreModeloFormulario();

    protected abstract String obtenerVistaListado();

    protected abstract String obtenerVistaCreacion();

    protected abstract String obtenerVistaEdicion();

    protected abstract String obtenerRedirectListado();
}

