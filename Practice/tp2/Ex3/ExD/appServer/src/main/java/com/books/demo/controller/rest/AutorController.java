package com.books.demo.controller.rest;

import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.AutorService;
import com.books.demo.controller.rest.dto.AutorDto;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    @Autowired
    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public ResponseEntity<?> listarAutoresActivos() {
        try {
            List<AutorDto> autores = autorService.listarActivos()
                    .stream()
                    .map(AutorDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(autores);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar autores.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return autorService.buscarPorId(id)
                    .map(AutorDto::fromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearAutor(@RequestBody AutorDto autorDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AutorDto.fromEntity(autorService.crearAutor(autorDto.toEntity())));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAutor(@PathVariable Long id, @RequestBody AutorDto autorDto) {
        try {
            return ResponseEntity.ok(AutorDto.fromEntity(autorService.modificarAutor(id, autorDto.toEntity())));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAutor(@PathVariable Long id) {
        try {
            autorService.eliminarAutor(id);
            return ResponseEntity.noContent().build();
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
