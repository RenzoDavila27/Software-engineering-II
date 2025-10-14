package com.books.demo.controller;

import com.books.demo.bussiness.logic.AutorService;
import com.books.demo.bussiness.logic.LibroService;
import com.books.demo.client.dto.AutorDto;
import com.books.demo.client.dto.LibroDto;
import com.books.demo.client.exception.ApiClientException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/autores")
public class AuthorsController {

    private static final String REDIRECT_AUTORES = "redirect:/autores";

    private final AutorService autorService;
    private final LibroService libroService;

    public AuthorsController(AutorService autorService, LibroService libroService) {
        this.autorService = autorService;
        this.libroService = libroService;
    }

    @GetMapping
    public String authors(Model model) {
        try {
            model.addAttribute("autores", autorService.listarAutores());
        } catch (ApiClientException ex) {
            model.addAttribute("autores", java.util.Collections.emptyList());
            Object existing = model.asMap().get("errorMessage");
            String message = existing != null
                    ? existing.toString() + " " + ex.getMessage()
                    : ex.getMessage();
            model.addAttribute("errorMessage", message);
        }
        return "authors";
    }

    @GetMapping("/nuevo")
    public String nuevoAutor(Model model) {
        if (!model.containsAttribute("autor")) {
            model.addAttribute("autor", new AutorDto());
        }
        return "authors-form";
    }

    @PostMapping
    public String crearAutor(@ModelAttribute AutorDto autorDto,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            autorService.crearAutor(autorDto);
            redirectAttributes.addFlashAttribute("successMessage", "Autor registrado correctamente.");
            return REDIRECT_AUTORES;
        } catch (IllegalArgumentException | ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("autor", autorDto);
            return "authors-form";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Ocurrio un error inesperado al crear el autor.");
            model.addAttribute("autor", autorDto);
            return "authors-form";
        }
    }

    @GetMapping("/{id}")
    public String detalleAutor(@PathVariable Long id,
                               @RequestParam(name = "mode", defaultValue = "consultar") String mode,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            return autorService.obtenerAutor(id)
                    .map(autor -> {
                        String normalizedMode = normalizarModo(mode);
                        model.addAttribute("autor", autor);
                        model.addAttribute("mode", normalizedMode);
                        model.addAttribute("pageTitle", tituloPorModo("Autor", normalizedMode));
                        if ("eliminar".equals(normalizedMode)) {
                            model.addAttribute("librosAutor", libroService.listarLibrosPorAutor(id));
                        }
                        return "authors-detail";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "El autor solicitado no existe.");
                        return REDIRECT_AUTORES;
                    });
        } catch (ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return REDIRECT_AUTORES;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al cargar el autor.");
            return REDIRECT_AUTORES;
        }
    }

    @PostMapping("/{id}/modificar")
    public String modificarAutor(@PathVariable Long id,
                                 @ModelAttribute AutorDto autorDto,
                                 RedirectAttributes redirectAttributes) {
        try {
            autorService.actualizarAutor(id, autorDto);
            redirectAttributes.addFlashAttribute("successMessage", "Autor actualizado correctamente.");
            return REDIRECT_AUTORES;
        } catch (IllegalArgumentException | ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/autores/" + id + "?mode=modificar";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al actualizar el autor.");
            return "redirect:/autores/" + id + "?mode=modificar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarAutor(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            libroService.eliminarLibrosPorAutor(id);
            List<LibroDto> librosAutor = libroService.listarLibrosPorAutor(id);
            int eliminados = librosAutor.size();
            libroService.eliminarLibrosPorAutor(id);
            autorService.eliminarAutor(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    eliminados > 0
                            ? "Autor eliminado junto con " + eliminados + " libro(s)."
                            : "Autor eliminado correctamente.");
            return REDIRECT_AUTORES;
        } catch (IllegalArgumentException | ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/autores/" + id + "?mode=eliminar";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al eliminar el autor.");
            return "redirect:/autores/" + id + "?mode=eliminar";
        }
    }

    private String normalizarModo(String mode) {
        if (mode == null) {
            return "consultar";
        }
        return switch (mode.toLowerCase()) {
            case "modificar", "editar" -> "modificar";
            case "eliminar", "borrar" -> "eliminar";
            default -> "consultar";
        };
    }

    private String tituloPorModo(String entidad, String mode) {
        return switch (mode) {
            case "modificar" -> "Modificar " + entidad;
            case "eliminar" -> "Eliminar " + entidad;
            default -> "Consultar " + entidad;
        };
    }
}