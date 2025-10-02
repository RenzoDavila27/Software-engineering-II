package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final DireccionService direccionService;

    public ProveedorController(ProveedorService proveedorService, DireccionService direccionService) {
        this.proveedorService = proveedorService;
        this.direccionService = direccionService;
    }

    @GetMapping("/listar")
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorService.listarProveedores());
        return "proveedor/listar";
    }

    @GetMapping("/crear")
    public String crearProveedorForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedor/crear";
    }

    @PostMapping("/crear")
    public String crearProveedor(@ModelAttribute Proveedor proveedor) {
        proveedorService.crearProveedor(proveedor);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarProveedorForm(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorService.buscarProveedorPorId(id));
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
    @GetMapping("/mapa/{direccionId}")
    public String verMapaDireccion(@PathVariable Long direccionId) {
        return direccionService.obtenerLinkGoogleMaps(direccionId)
                .map(url -> "redirect:" + url)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La direccion no tiene coordenadas disponibles"));
    }
}
