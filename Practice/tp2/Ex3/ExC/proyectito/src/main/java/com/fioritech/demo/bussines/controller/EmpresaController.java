package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.CrudTemplateController;
import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Empresa;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.EmpresaService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
import com.fioritech.demo.bussines.logic.service.PaisService;
import com.fioritech.demo.bussines.logic.service.ProvinciaService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Controller
@RequestMapping("/empresa")
public class EmpresaController extends CrudTemplateController<Empresa, Long> {

    private final EmpresaService empresaService;
    private final DireccionService direccionService;
    private final LocalidadService localidadService;
    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public EmpresaController(EmpresaService empresaService,
                             DireccionService direccionService,
                             LocalidadService localidadService,
                             DepartamentoService departamentoService,
                             ProvinciaService provinciaService,
                             PaisService paisService) {
        this.empresaService = empresaService;
        this.direccionService = direccionService;
        this.localidadService = localidadService;
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @Override
    protected Collection<Empresa> listarEntidades() {
        return empresaService.listarEmpresas();
    }

    @Override
    protected void crearEntidad(Empresa empresa) {
        empresaService.crearEmpresa(empresa);
    }

    @Override
    protected Empresa buscarEntidad(Long id) {
        return empresaService.buscarEmpresaPorId(id);
    }

    @Override
    protected void modificarEntidad(Long id, Empresa cambios) {
        empresaService.modificarEmpresa(id, cambios);
    }

    @Override
    protected void eliminarEntidad(Long id) {
        empresaService.eliminarEmpresa(id);
    }

    @Override
    protected Empresa crearInstanciaFormulario() {
        Empresa empresa = new Empresa();
        inicializarDireccion(empresa);
        return empresa;
    }

    @Override
    protected void prepararInstanciaExistente(Empresa empresa) {
        if (empresa.getDireccion() == null) {
            inicializarDireccion(empresa);
        }
    }

    @Override
    protected String obtenerNombreModeloListado() {
        return "empresas";
    }

    @Override
    protected String obtenerNombreModeloFormulario() {
        return "empresa";
    }

    @Override
    protected String obtenerVistaListado() {
        return "empresa/listar";
    }

    @Override
    protected String obtenerVistaCreacion() {
        return "empresa/crear";
    }

    @Override
    protected String obtenerVistaEdicion() {
        return "empresa/modificar";
    }

    @Override
    protected String obtenerRedirectListado() {
        return "redirect:/empresa/listar";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarEmpresas() {
        byte[] contenido = empresaService.exportarEmpresasExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "empresas.xlsx");
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(contenido.length)
                .body(contenido);
    }

    @ModelAttribute("localidades")
    public Collection<Localidad> cargarLocalidades() {
        return localidadService.listarLocalidades();
    }

    @ModelAttribute("departamentos")
    public Collection<Departamento> cargarDepartamentos() {
        return departamentoService.listarDepartamentos();
    }

    @ModelAttribute("provincias")
    public Collection<Provincia> cargarProvincias() {
        return provinciaService.listarProvincias();
    }

    @ModelAttribute("paises")
    public Collection<Pais> cargarPaises() {
        return paisService.listarPaises();
    }

    @GetMapping("/mapa/{direccionId}")
    public String verMapaDireccion(@PathVariable Long direccionId) {
        return direccionService.obtenerLinkGoogleMaps(direccionId)
                .map(url -> "redirect:" + url)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La direccion no tiene coordenadas disponibles"));
    }

    private void inicializarDireccion(Empresa empresa) {
        Direccion direccion = new Direccion();
        Localidad localidad = new Localidad();
        Departamento departamento = new Departamento();
        Provincia provincia = new Provincia();
        Pais pais = new Pais();

        provincia.setPais(pais);
        departamento.setProvincia(provincia);
        localidad.setDepartamento(departamento);
        direccion.setLocalidad(localidad);
        empresa.setDireccion(direccion);
    }
}

