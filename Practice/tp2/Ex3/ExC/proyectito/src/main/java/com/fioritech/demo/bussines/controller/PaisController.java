package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.logic.service.PaisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/pais")
public class PaisController extends CrudTemplateController<Pais, Long> {

    private final PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @Override
    protected Collection<Pais> listarEntidades() {
        return paisService.listarPaises();
    }

    @Override
    protected void crearEntidad(Pais pais) {
        paisService.crearPais(pais);
    }

    @Override
    protected Pais buscarEntidad(Long id) {
        return paisService.buscarPaisPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Pais cambios) {
        paisService.modificarPais(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        paisService.eliminarPais(id);
    }

    @Override
    protected Pais crearInstanciaFormulario() {
        return new Pais();
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "listaPais";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "pais";
    }

    @Override
    protected String obtenerVistaListado() {
        return "direccion/pais/listarPais";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "direccion/pais/crearPais";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "direccion/pais/editarPais";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/pais/listar";
    }

    @GetMapping("/modificarForm/{id}")
    public String modificarFormularioAlternativo(@PathVariable Long id, Model model) {
        return super.mostrarFormularioEdicion(id, model);
    }

    @GetMapping("/volverEdit")
    public String volver() {
        return obtenerRedirectListado();
    }
}

