package com.fioritech.demo.bussines.controller.template;

import org.springframework.ui.Model;

/**
 * Template Method básico para controladores que solo deben renderizar una vista
 * y eventualmente preparar información adicional en el modelo.
 */
public abstract class SimpleTemplateController {

    /**
     * Template Method que orquesta la renderización de la vista.
     */
    protected final String render(Model model) {
        prepararModelo(model);
        return obtenerVista();
    }

    /**
     * Hook opcional para cargar datos en el modelo antes de renderizar.
     */
    protected void prepararModelo(Model model) {
        // Hook opcional
    }

    /**
     * Vista que se debe devolver para el controlador.
     */
    protected abstract String obtenerVista();
}

