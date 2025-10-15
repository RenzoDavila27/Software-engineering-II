package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/direccion")
public class DireccionController extends CrudTemplateController<Direccion, Long> {

    private final DireccionService direccionService;
    private final LocalidadService localidadService;

    public DireccionController(DireccionService direccionService, LocalidadService localidadService) {
        this.direccionService = direccionService;
        this.localidadService = localidadService;
    }

    @Override
    protected Collection<Direccion> listarEntidades() {
        return direccionService.listarDirecciones();
    }

    @Override
    protected void crearEntidad(Direccion direccion) {
        direccionService.crearDireccion(direccion);
    }

    @Override
    protected Direccion buscarEntidad(Long id) {
        return direccionService.buscarDireccionPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Direccion cambios) {
        direccionService.modificarDireccion(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        direccionService.eliminarDireccion(id);
    }

    @Override
    protected Direccion crearInstanciaFormulario() {
        return new Direccion();
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "direcciones";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "direccion";
    }

    @Override
    protected String obtenerVistaListado() {
        return "direccion/listar";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "direccion/crear";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "direccion/modificar";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/direccion/listar";
    }

    @ModelAttribute("localidades")
    public Collection<Localidad> cargarLocalidades() {
        return localidadService.listarLocalidades();
    }
}
