package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.service.PaisService;
import com.fioritech.demo.bussines.logic.service.ProvinciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/provincia")
public class ProvinciaController extends CrudTemplateController<Provincia, Long> {

    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public ProvinciaController(ProvinciaService provinciaService, PaisService paisService) {
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @Override
    protected Collection<Provincia> listarEntidades() {
        return provinciaService.listarProvincias();
    }

    @Override
    protected void crearEntidad(Provincia provincia) {
        provinciaService.crearProvincia(provincia);
    }

    @Override
    protected Provincia buscarEntidad(Long id) {
        return provinciaService.buscarProvinciaPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Provincia cambios) {
        provinciaService.modificarProvincia(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        provinciaService.eliminarProvincia(id);
    }

    @Override
    protected Provincia crearInstanciaFormulario() {
        Provincia provincia = new Provincia();
        provincia.setPais(new Pais());
        return provincia;
    }

    @Override
    protected void prepararInstanciaExistente(Provincia provincia) {
        if (provincia.getPais() == null) {
            provincia.setPais(new Pais());
        }
    }

    @Override
    protected void prepararModeloFormulario(Model model) {
        model.addAttribute("listaPais", paisService.listarPaises());
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "listaProvincia";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "provincia";
    }

    @Override
    protected String obtenerVistaListado() {
        return "direccion/provincia/listarProvincia";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "direccion/provincia/crearProvincia";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "direccion/provincia/editarProvincia";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/provincia/listar";
    }

    @GetMapping("/modificarForm/{id}")
    public String modificarFormularioAlternativo(@PathVariable Long id, Model model) {
        return super.mostrarFormularioEdicion(id, model);
    }

    @GetMapping("/volverEdit")
    public String volver() {
        return obtenerRedirectListado();
    }

    @PostMapping("/crearForm")
    public String crearFormularioAlternativo(@ModelAttribute Provincia provincia) {
        return super.crear(provincia);
    }

    @ModelAttribute("listaPais")
    public Collection<Pais> cargarPaises() {
        return paisService.listarPaises();
    }
}
