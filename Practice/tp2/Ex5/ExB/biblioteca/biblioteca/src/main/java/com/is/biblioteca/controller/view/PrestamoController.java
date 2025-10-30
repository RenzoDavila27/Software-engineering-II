package com.is.biblioteca.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.domain.entity.Prestamo;
import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.domain.enumeration.Rol;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.service.LibroService;
import com.is.biblioteca.business.logic.service.PrestamoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;
    
    @Autowired
    private LibroService libroService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lista")
    public String listarPrestamos(ModelMap modelo) {
        try {
            List<Prestamo> prestamos = prestamoService.listarPrestamos();
            modelo.addAttribute("prestamos", prestamos);
            modelo.addAttribute("vistaAdmin", true);
            modelo.addAttribute("titulo", "Listado de Préstamos");
            return "prestamo_list";
        } catch (ErrorServiceException e) {
            modelo.put("error", e.getMessage());
            return "prestamo_list";
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/mis")
    public String listarPrestamosUsuario(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario == null) {
            return "redirect:/login";
        }
        try {
            List<Prestamo> prestamos = prestamoService.listarPrestamosPorUsuario(usuario.getId());
            modelo.addAttribute("prestamos", prestamos);
            modelo.addAttribute("vistaAdmin", usuario.getRol() == Rol.ADMIN);
            modelo.addAttribute("propios", true);
            modelo.addAttribute("titulo", "Mis Préstamos");
            return "prestamo_list";
        } catch (ErrorServiceException e) {
            modelo.put("error", e.getMessage());
            return "prestamo_list";
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/solicitar/{idLibro}")
    public String verFormularioPrestamo(@PathVariable String idLibro,
                                        ModelMap modelo,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario == null) {
            return "redirect:/login";
        }
        try {
            Libro libro = libroService.buscarLibro(idLibro);
            if (libro == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontró el libro indicado");
                return "redirect:/libro/lista";
            }
            Integer disponibles = libro.getEjemplaresRestantes();
            if (disponibles == null) {
                disponibles = libro.getEjemplares() != null ? libro.getEjemplares() : 0;
            }
            if (disponibles <= 0) {
                redirectAttributes.addFlashAttribute("error", "No hay ejemplares disponibles para el préstamo");
                return "redirect:/libro/lista";
            }
            modelo.addAttribute("libro", libro);
            modelo.addAttribute("disponibles", disponibles);
            return "prestamo_form";
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/libro/lista";
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/solicitar")
    public String solicitarPrestamo(@RequestParam String idLibro,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            prestamoService.crearPrestamo(idLibro, usuario.getId());
            redirectAttributes.addFlashAttribute("exito", "El préstamo se registró correctamente");
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/prestamo/mis";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/devolver/{idPrestamo}")
    public String devolverPrestamo(@PathVariable String idPrestamo,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario == null) {
            return "redirect:/login";
        }

        boolean esAdmin = usuario.getRol() == Rol.ADMIN;
        String redirect = esAdmin ? "redirect:/prestamo/lista" : "redirect:/prestamo/mis";

        try {
            Prestamo prestamo = prestamoService.buscarPrestamo(idPrestamo);
            if (!esAdmin && prestamo.getUsuario() != null && !prestamo.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No posee permisos para devolver este préstamo");
                return redirect;
            }

            prestamoService.devolverPrestamo(idPrestamo);
            redirectAttributes.addFlashAttribute("exito", "El préstamo fue devuelto correctamente");
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return redirect;
    }
}
