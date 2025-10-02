package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.ProveedorService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
import com.fioritech.demo.bussines.logic.service.ProvinciaService;
import com.fioritech.demo.bussines.logic.service.PaisService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final DireccionService direccionService;
    private final LocalidadService localidadService;
    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public ProveedorController(ProveedorService proveedorService,
                               DireccionService direccionService,
                               LocalidadService localidadService,
                               DepartamentoService departamentoService,
                               ProvinciaService provinciaService,
                               PaisService paisService) {
        this.proveedorService = proveedorService;
        this.direccionService = direccionService;
        this.localidadService = localidadService;
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @GetMapping("/listar")
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorService.listarProveedores());
        return "proveedor/listar";
    }

    @GetMapping("/crear")
    public String crearProveedorForm(Model model) {
        Proveedor proveedor = new Proveedor();
        Direccion direccion = new Direccion();
        Localidad localidad = new Localidad();
        Departamento departamento = new Departamento();
        Provincia provincia = new Provincia();
        Pais pais = new Pais();

        provincia.setPais(pais);
        departamento.setProvincia(provincia);
        localidad.setDepartamento(departamento);
        direccion.setLocalidad(localidad);
        proveedor.setDireccion(direccion);

        model.addAttribute("proveedor", proveedor);
        return "proveedor/crear";
    }

    @PostMapping("/crear")
    public String crearProveedor(@ModelAttribute Proveedor proveedor) {
        proveedorService.crearProveedor(proveedor);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarProveedorForm(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorService.buscarProveedorPorId(id);
        if (proveedor.getDireccion() == null) {
            Direccion direccion = new Direccion();
            Localidad localidad = new Localidad();
            Departamento departamento = new Departamento();
            Provincia provincia = new Provincia();
            Pais pais = new Pais();
            provincia.setPais(pais);
            departamento.setProvincia(provincia);
            localidad.setDepartamento(departamento);
            direccion.setLocalidad(localidad);
            proveedor.setDireccion(direccion);
        }
        model.addAttribute("proveedor", proveedor);
        return "proveedor/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarProveedor(@PathVariable Long id, @ModelAttribute Proveedor cambios) {
        proveedorService.modificarProveedor(id, cambios);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/mapa/{direccionId}")
    public String verMapaDireccion(@PathVariable Long direccionId) {
        return direccionService.obtenerLinkGoogleMaps(direccionId)
                .map(url -> "redirect:" + url)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La direccion no tiene coordenadas disponibles"));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarProveedores() {
        byte[] contenido = proveedorService.exportarProveedoresPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "proveedores.pdf");
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
}
