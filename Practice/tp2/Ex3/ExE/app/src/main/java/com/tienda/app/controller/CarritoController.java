package com.tienda.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.domain.CarritoItem;
import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.ArticuloService;
import com.tienda.app.business.logic.service.CarritoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ArticuloService articuloService;

    public CarritoController(CarritoService carritoService, ArticuloService articuloService) {
        this.carritoService = carritoService;
        this.articuloService = articuloService;
    }

    @GetMapping("/list")
    public String verCarrito(Model model, HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = obtenerUsuario(session);
        if (usuario == null) {
            attributes.addFlashAttribute("msgError", "Debes iniciar sesión para ver tu carrito.");
            return "redirect:/login?redirect=/carrito/list";
        }
        try {
            List<CarritoItem> items = carritoService.listarItems(usuario);
            Double total = carritoService.obtenerTotal(usuario);
            model.addAttribute("items", items);
            model.addAttribute("total", total);
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }
        return "view/lCarrito";
    }

    @PostMapping("/agregar")
    public String agregarArticulo(@RequestParam("articuloId") Long articuloId,
                                  @RequestParam(value = "cantidad", defaultValue = "1") Integer cantidad,
                                  HttpSession session,
                                  RedirectAttributes attributes,
                                  @RequestParam(value = "redirect", required = false) String redirect) {
        Usuario usuario = obtenerUsuario(session);
        if (usuario == null) {
            attributes.addFlashAttribute("msgError", "Debes iniciar sesión para agregar productos al carrito.");
            return "redirect:/login?redirect=" + (redirect != null ? redirect : "/");
        }
        try {
            Articulo articulo = articuloService.obtenerEntidad(articuloId);
            carritoService.agregarArticuloAlCarrito(usuario, articulo, cantidad != null ? cantidad : 1);
            attributes.addFlashAttribute("msgExito", "Artículo agregado al carrito.");
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }
        return "redirect:/carrito/list";
    }

    @PostMapping("/eliminar-item/{itemId}")
    public String eliminarItem(@PathVariable("itemId") Long itemId,
                               HttpSession session,
                               RedirectAttributes attributes) {
        Usuario usuario = obtenerUsuario(session);
        if (usuario == null) {
            attributes.addFlashAttribute("msgError", "Debes iniciar sesión para modificar tu carrito.");
            return "redirect:/login?redirect=/carrito/list";
        }
        try {
            carritoService.eliminarItem(itemId, usuario);
            attributes.addFlashAttribute("msgExito", "Se eliminó el artículo del carrito.");
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/carrito/list";
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = obtenerUsuario(session);
        if (usuario == null) {
            attributes.addFlashAttribute("msgError", "Debes iniciar sesión para modificar tu carrito.");
            return "redirect:/login?redirect=/carrito/list";
        }
        try {
            carritoService.vaciarCarrito(usuario);
            attributes.addFlashAttribute("msgExito", "Se vació el carrito correctamente.");
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/carrito/list";
    }

    private Usuario obtenerUsuario(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object usuario = session.getAttribute("usuarioActual");
        if (usuario instanceof Usuario usuarioActual) {
            return usuarioActual;
        }
        return null;
    }
}
