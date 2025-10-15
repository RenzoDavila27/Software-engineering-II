package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
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
@RequestMapping("/localidad")
public class LocalidadController extends CrudTemplateController<Localidad, Long> {

    private final LocalidadService localidadService;
    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public LocalidadController(LocalidadService localidadService,
                               DepartamentoService departamentoService,
                               ProvinciaService provinciaService,
                               PaisService paisService) {
        this.localidadService = localidadService;
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @Override
    protected Collection<Localidad> listarEntidades() {
        return localidadService.listarLocalidades();
    }

    @Override
    protected void crearEntidad(Localidad localidad) {
        localidadService.crearLocalidad(localidad);
    }

    @Override
    protected Localidad buscarEntidad(Long id) {
        return localidadService.buscarLocalidadPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Localidad cambios) {
        localidadService.modificarLocalidad(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        localidadService.eliminarLocalidad(id);
    }

    @Override
    protected Localidad crearInstanciaFormulario() {
        Localidad localidad = new Localidad();
        inicializarDepartamento(localidad);
        return localidad;
    }

    @Override
    protected void prepararInstanciaExistente(Localidad localidad) {
        if (localidad.getDepartamento() == null) {
            inicializarDepartamento(localidad);
        } else {
            if (localidad.getDepartamento().getProvincia() == null) {
                localidad.getDepartamento().setProvincia(new Provincia());
            }
            if (localidad.getDepartamento().getProvincia().getPais() == null) {
                localidad.getDepartamento().getProvincia().setPais(new Pais());
            }
        }
    }

    @Override
    protected void prepararModeloFormulario(Model model) {
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentos());
        model.addAttribute("listaProvincia", provinciaService.listarProvincias());
        model.addAttribute("listaPais", paisService.listarPaises());
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "listaLocalidad";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "localidad";
    }

    @Override
    protected String obtenerVistaListado() {
        return "direccion/localidad/listarLocalidad";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "direccion/localidad/crearLocalidad";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "direccion/localidad/editarLocalidad";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/localidad/listar";
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

    @ModelAttribute("listaDepartamento")
    public Collection<Departamento> cargarDepartamentos() {
        return departamentoService.listarDepartamentos();
    }

    @ModelAttribute("listaProvincia")
    public Collection<Provincia> cargarProvincias() {
        return provinciaService.listarProvincias();
    }

    @ModelAttribute("listaPais")
    public Collection<Pais> cargarPaises() {
        return paisService.listarPaises();
    }

    private void inicializarDepartamento(Localidad localidad) {
        Departamento departamento = new Departamento();
        Provincia provincia = new Provincia();
        provincia.setPais(new Pais());
        departamento.setProvincia(provincia);
        localidad.setDepartamento(departamento);
    }
}

