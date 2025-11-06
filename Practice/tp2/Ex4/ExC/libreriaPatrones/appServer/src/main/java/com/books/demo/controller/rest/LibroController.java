package com.books.demo.controller.rest;

import com.books.demo.bussiness.logic.adapter.LibroAdapter;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.strategy.libro.LibroBusquedaTipo;
import com.books.demo.bussiness.logic.service.LibroService;
import com.books.demo.controller.rest.dto.LibroDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private final LibroService libroService;

    private final LibroAdapter libroAdapter;

    public LibroController(LibroService libroService, LibroAdapter libroAdapter) {
        this.libroService = libroService;
        this.libroAdapter = libroAdapter;
    }

    @GetMapping
    public ResponseEntity<?> listarLibrosActivos() {
        try {
            List<LibroDto> libros = libroService.listarActivos()
                    .stream()
                    .map(libroAdapter::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(libros);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar libros.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return libroService.buscarPorId(id)
                    .map(libroAdapter::toDto)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/sin-asignar")
    public ResponseEntity<?> buscarLibrosSinAsignar() {
        try {
            List<LibroDto> libros = libroService.buscarLibrosSinAsignar()
                    .stream()
                    .map(libroAdapter::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(libros);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar libros sin asignar.");
        }
    }

    @GetMapping("/asignados")
    public ResponseEntity<?> buscarLibrosAsignados() {
        try {
            List<LibroDto> libros = libroService.buscarLibrosAsignados()
                    .stream()
                    .map(libroAdapter::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(libros);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar libros asignados.");
        }
    }

    @GetMapping("/autor/{autorId}/iterador")
    public ResponseEntity<?> buscarLibrosDeAutorConIterador(@PathVariable Long autorId) {
        try {
            List<LibroDto> libros = libroService.recorrerLibrosPorAutor(autorId)
                    .stream()
                    .map(libroAdapter::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(libros);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar libros por autor.");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarLibrosPor(@RequestParam LibroBusquedaTipo tipo,
                                             @RequestParam String valor) {
        try {
            List<LibroDto> libros = libroService.buscarLibrosPor(tipo, valor)
                    .stream()
                    .map(libroAdapter::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(libros);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar libros.");
        }
    }

    @PostMapping
    public ResponseEntity<?> crearLibro(@RequestBody LibroDto libroDto) {
        try {
            var libro = libroAdapter.toEntity(libroDto);
            var creado = libroService.crearLibro(libro);
            return ResponseEntity.status(HttpStatus.CREATED).body(libroAdapter.toDto(creado));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLibro(@PathVariable Long id, @RequestBody LibroDto libroDto) {
        try {
            var datos = libroAdapter.toEntity(libroDto);
            datos.setId(id);
            var actualizado = libroService.modificarLibro(id, datos);
            return ResponseEntity.ok(libroAdapter.toDto(actualizado));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        try {
            libroService.eliminarLibro(id);
            return ResponseEntity.noContent().build();
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
