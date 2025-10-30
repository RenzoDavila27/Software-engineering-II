package com.books.demo.controller.rest;

import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.logic.service.LibroService;
import com.books.demo.controller.rest.dto.LibroDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<LibroDto>> listarLibrosActivos() {
        List<LibroDto> libros = libroService.listarActivos()
                .stream()
                .map(LibroDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibroDto> buscarPorId(@PathVariable Long id) {
        return libroService.buscarPorId(id)
                .map(LibroDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/sin-asignar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LibroDto>> buscarLibrosSinAsignar() {
        List<LibroDto> libros = libroService.buscarLibrosSinAsignar()
                .stream()
                .map(LibroDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/asignados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LibroDto>> buscarLibrosAsignados() {
        List<LibroDto> libros = libroService.buscarLibrosAsignados()
                .stream()
                .map(LibroDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(libros);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearLibro(@RequestBody LibroDto libroDto) {
        try {
            Libro libro = libroDto.toEntity();
            libro.setPersona(libroService.obtenerPersona(libroDto.getPersonaId()));
            libro.setAutores(libroService.obtenerAutores(libroDto.getAutoresIds()));
            Libro creado = libroService.crearLibro(libro);
            return ResponseEntity.status(HttpStatus.CREATED).body(LibroDto.fromEntity(creado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarLibro(@PathVariable Long id, @RequestBody LibroDto libroDto) {
        try {
            Libro datos = libroDto.toEntity();
            datos.setPersona(libroService.obtenerPersona(libroDto.getPersonaId()));
            datos.setAutores(libroService.obtenerAutores(libroDto.getAutoresIds()));
            Libro actualizado = libroService.modificarLibro(id, datos);
            return ResponseEntity.ok(LibroDto.fromEntity(actualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        try {
            libroService.eliminarLibro(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
