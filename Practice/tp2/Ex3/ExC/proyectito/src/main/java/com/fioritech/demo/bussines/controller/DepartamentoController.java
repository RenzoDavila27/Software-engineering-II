package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
import com.fioritech.demo.bussines.logic.service.PaisService;
import com.fioritech.demo.bussines.logic.service.ProvinciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
@RequestMapping("/departamento")
public class DepartamentoController extends CrudTemplateController<Departamento, Long> {

    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public DepartamentoController(DepartamentoService departamentoService,
                                  ProvinciaService provinciaService,
                                  PaisService paisService) {
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @Override
    protected Collection<Departamento> listarEntidades() {
        return departamentoService.listarDepartamentos();
    }

    @Override
    protected void crearEntidad(Departamento departamento) {
        departamentoService.crearDepartamento(departamento);
    }

    @Override
    protected Departamento buscarEntidad(Long id) {
        return departamentoService.buscarDepartamentoPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Departamento cambios) {
        departamentoService.modificarDepartamento(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        departamentoService.eliminarDepartamento(id);
    }

    @Override
    protected Departamento crearInstanciaFormulario() {
        Departamento departamento = new Departamento();
        inicializarProvincia(departamento);
        return departamento;
    }

    @Override
    protected void prepararInstanciaExistente(Departamento departamento) {
        if (departamento.getProvincia() == null) {
            inicializarProvincia(departamento);
        } else if (departamento.getProvincia().getPais() == null) {
            departamento.getProvincia().setPais(new Pais());
        }
    }

    @Override
    protected void prepararModeloFormulario(Model model) {
        model.addAttribute("listaProvincia", provinciaService.listarProvincias());
        model.addAttribute("listaPais", paisService.listarPaises());
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "listaDepartamento";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "departamento";
    }

    @Override
    protected String obtenerVistaListado() {
        return "direccion/departamento/listarDepartamento";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "direccion/departamento/crearDepartamento";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "direccion/departamento/editarDepartamento";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/departamento/listar";
    }

    @GetMapping("/crearForm")
    public String crearFormularioAlternativo(Model model) {
        return super.mostrarFormularioCreacion(model);
    }

    @GetMapping("/modificarForm/{id}")
    public String modificarFormularioAlternativo(@PathVariable Long id, Model model) {
        return super.mostrarFormularioEdicion(id, model);
    }

    @GetMapping("/volverEdit")
    public String volver() {
        return obtenerRedirectListado();
    }

    private void inicializarProvincia(Departamento departamento) {
        Provincia provincia = new Provincia();
        provincia.setPais(new Pais());
        departamento.setProvincia(provincia);
    }

    @ModelAttribute("listaProvincia")
    public Collection<Provincia> cargarProvincias() {
        return provinciaService.listarProvincias();
    }

    @ModelAttribute("listaPais")
    public Collection<Pais> cargarPaises() {
        return paisService.listarPaises();
    }
}

