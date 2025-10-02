package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Empresa;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.EmpresaService;
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

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final DireccionService direccionService;

    public EmpresaController(EmpresaService empresaService, DireccionService direccionService) {
        this.empresaService = empresaService;
        this.direccionService = direccionService;
    }

    @GetMapping("/listar")
    public String listarEmpresas(Model model) {
        model.addAttribute("empresas", empresaService.listarEmpresas());
        return "empresa/listar";
    }

    @GetMapping("/crear")
    public String crearEmpresaForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresa/crear";
    }

    @PostMapping("/crear")
    public String crearEmpresa(@ModelAttribute Empresa empresa) {
        empresaService.crearEmpresa(empresa);
        return "redirect:/empresa/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarEmpresaForm(@PathVariable Long id, Model model) {
        model.addAttribute("empresa", empresaService.buscarEmpresaPorId(id));
        return "empresa/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarEmpresa(@PathVariable Long id, @ModelAttribute Empresa cambios) {
        empresaService.modificarEmpresa(id, cambios);
        return "redirect:/empresa/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
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
    @GetMapping("/mapa/{direccionId}")
    public String verMapaDireccion(@PathVariable Long direccionId) {
        return direccionService.obtenerLinkGoogleMaps(direccionId)
                .map(url -> "redirect:" + url)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La direccion no tiene coordenadas disponibles"));
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

    @ModelAttribute("direcciones")
    public Collection<Direccion> cargarDirecciones() {
        return direccionService.listarDirecciones();
    }
}
