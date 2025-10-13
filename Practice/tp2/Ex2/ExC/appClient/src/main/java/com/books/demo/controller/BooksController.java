package com.books.demo.controller;

import com.books.demo.bussiness.logic.AutorService;
import com.books.demo.bussiness.logic.LibroService;
import com.books.demo.bussiness.logic.PersonaService;
import com.books.demo.client.dto.ArchivoLibroDto;
import com.books.demo.client.dto.LibroDto;
import com.books.demo.client.exception.ApiClientException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/libros")
public class BooksController {

    private static final String REDIRECT_LIBROS = "redirect:/libros";

    private final LibroService libroService;
    private final AutorService autorService;
    private final PersonaService personaService;

    public BooksController(LibroService libroService,
                           AutorService autorService,
                           PersonaService personaService) {
        this.libroService = libroService;
        this.autorService = autorService;
        this.personaService = personaService;
    }

    @GetMapping
    public String books(Model model) {
        StringBuilder errores = new StringBuilder();
        try {
            model.addAttribute("libros", libroService.listarLibros());
        } catch (ApiClientException ex) {
            model.addAttribute("libros", java.util.Collections.emptyList());
            errores.append(ex.getMessage());
        }
        try {
            model.addAttribute("autores", autorService.listarAutores());
        } catch (ApiClientException ex) {
            model.addAttribute("autores", java.util.Collections.emptyList());
            if (errores.length() > 0) {
                errores.append(" ");
            }
            errores.append(ex.getMessage());
        }
        if (errores.length() > 0) {
            Object existing = model.asMap().get("errorMessage");
            String message = existing != null
                    ? existing.toString() + " " + errores
                    : errores.toString();
            model.addAttribute("errorMessage", message);
        }
        return "books";
    }

    @GetMapping("/nuevo")
    public String nuevoLibro(Model model) {
        prepararFormularioLibro(new LibroDto(), model);
        return "books-form";
    }

    @PostMapping
    public String crearLibro(@ModelAttribute LibroDto libroDto,
                             @RequestParam(value = "archivoPdf", required = false) MultipartFile archivoPdf,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            if (libroDto.getAutoresIds() == null) {
                libroDto.setAutoresIds(new java.util.ArrayList<>());
            }
            libroService.crearLibro(libroDto, archivoPdf);
            redirectAttributes.addFlashAttribute("successMessage", "Libro registrado correctamente.");
            return REDIRECT_LIBROS;
        } catch (IllegalArgumentException | ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormularioLibro(libroDto, model);
            return "books-form";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Ocurrio un error inesperado al crear el libro.");
            prepararFormularioLibro(libroDto, model);
            return "books-form";
        }
    }

    @GetMapping("/{id}")
    public String detalleLibro(@PathVariable Long id,
                               @RequestParam(name = "mode", defaultValue = "consultar") String mode,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            return libroService.obtenerLibro(id)
                    .map(libro -> prepararVistaDetalle(libro, mode, model))
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "El libro solicitado no existe.");
                        return REDIRECT_LIBROS;
                    });
        } catch (ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LIBROS;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al cargar el libro.");
            return REDIRECT_LIBROS;
        }
    }

    @PostMapping("/{id}/modificar")
    public String modificarLibro(@PathVariable Long id,
                                 @ModelAttribute LibroDto libroDto,
                                 @RequestParam(value = "archivoPdf", required = false) MultipartFile archivoPdf,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (libroDto.getAutoresIds() == null) {
                libroDto.setAutoresIds(new java.util.ArrayList<>());
            }
            libroService.actualizarLibro(id, libroDto, archivoPdf);
            redirectAttributes.addFlashAttribute("successMessage", "Libro actualizado correctamente.");
            return REDIRECT_LIBROS;
        } catch (IllegalArgumentException | ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/libros/" + id + "?mode=modificar";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al actualizar el libro.");
            return "redirect:/libros/" + id + "?mode=modificar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarLibro(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            libroService.eliminarLibro(id);
            redirectAttributes.addFlashAttribute("successMessage", "Libro eliminado correctamente.");
            return REDIRECT_LIBROS;
        } catch (IllegalArgumentException | ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/libros/" + id + "?mode=eliminar";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al eliminar el libro.");
            return "redirect:/libros/" + id + "?mode=eliminar";
        }
    }

    @GetMapping("/{id}/archivo/ver")
    public String verArchivo(@PathVariable Long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            return libroService.obtenerLibro(id)
                    .map(libro -> prepararVistaArchivo(libro, model))
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "El libro solicitado no existe.");
                        return REDIRECT_LIBROS;
                    });
        } catch (ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LIBROS;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al cargar el archivo del libro.");
            return REDIRECT_LIBROS;
        }
    }

    @GetMapping("/{id}/archivo/stream")
    @ResponseBody
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        try {
            ArchivoLibroDto archivo = libroService.obtenerArchivo(id)
                    .orElseThrow(() -> new ApiClientException("El libro no tiene un archivo PDF asociado."));
            byte[] datos = libroService.descargarArchivo(id);
            if (datos == null || datos.length == 0) {
                throw new ApiClientException("El archivo PDF del libro esta vacio o no pudo descargarse.");
            }
            String nombreArchivo = archivo.getNombreArchivo() != null ? archivo.getNombreArchivo() : "libro.pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(datos);
        } catch (ApiClientException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
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

    private String prepararVistaDetalle(LibroDto libro, String mode, Model model) {
        String normalizedMode = normalizarModo(mode);
        model.addAttribute("libro", libro);
        model.addAttribute("mode", normalizedMode);
        model.addAttribute("pageTitle", tituloPorModo("Libro", normalizedMode));

        // Personas disponibles para asignar el libro
        try {
            List<com.books.demo.client.dto.PersonaDto> personas = personaService.listarPersonas();
            model.addAttribute("personasDisponibles", personas);
            if (libro.getPersonaId() != null) {
                personas.stream()
                        .filter(p -> libro.getPersonaId().equals(p.getId()))
                        .findFirst()
                        .ifPresent(p -> model.addAttribute("personaAsignada", p));
            }
        } catch (ApiClientException ex) {
            model.addAttribute("personasDisponibles", java.util.Collections.emptyList());
            acumularMensaje(model, ex.getMessage());
        }

        if (libro.getPersonaId() != null && model.asMap().get("personaAsignada") == null) {
            try {
                personaService.obtenerPersona(libro.getPersonaId())
                        .ifPresent(p -> model.addAttribute("personaAsignada", p));
            } catch (ApiClientException ex) {
                acumularMensaje(model, ex.getMessage());
            }
        }

        try {
            model.addAttribute("autoresDisponibles", autorService.listarAutores());
        } catch (ApiClientException ex) {
            model.addAttribute("autoresDisponibles", java.util.Collections.emptyList());
            acumularMensaje(model, ex.getMessage());
        }

        return "books-detail";
    }

    private String prepararVistaArchivo(LibroDto libro, Model model) {
        model.addAttribute("libro", libro);
        model.addAttribute("archivo", libro.getArchivo());
        if (libro.getArchivo() == null) {
            model.addAttribute("errorMessage", "El libro seleccionado no tiene un PDF disponible.");
        }
        model.addAttribute("pageTitle", "Visualizar PDF - " + libro.getTitulo());
        return "books-pdf";
    }

    private void prepararFormularioLibro(LibroDto libro, Model model) {
        if (libro.getAutoresIds() == null) {
            libro.setAutoresIds(new ArrayList<>());
        }
        model.addAttribute("libro", libro);
        try {
            List<com.books.demo.client.dto.PersonaDto> personas = personaService.listarPersonas();
            model.addAttribute("personasDisponibles", personas);
        } catch (ApiClientException ex) {
            model.addAttribute("personasDisponibles", java.util.Collections.emptyList());
            acumularMensaje(model, ex.getMessage());
        }
        try {
            model.addAttribute("autoresDisponibles", autorService.listarAutores());
        } catch (ApiClientException ex) {
            model.addAttribute("autoresDisponibles", java.util.Collections.emptyList());
            acumularMensaje(model, ex.getMessage());
        }
    }

    private void acumularMensaje(Model model, String nuevoMensaje) {
        Object existing = model.asMap().get("errorMessage");
        String message = existing != null
                ? existing.toString() + " " + nuevoMensaje
                : nuevoMensaje;
        model.addAttribute("errorMessage", message);
    }
}
