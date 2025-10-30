package com.books.demo.controller.rest;

import com.books.demo.bussiness.logic.service.LocalidadService;
import com.books.demo.controller.rest.dto.LocalidadDto;
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
@RequestMapping("/api/localidades")
public class LocalidadController {

    private final LocalidadService localidadService;
    
    public LocalidadController(LocalidadService localidadService) {
        this.localidadService = localidadService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LocalidadDto>> listarLocalidadesActivas() {
        List<LocalidadDto> localidades = localidadService.listarActivas()
                .stream()
                .map(LocalidadDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(localidades);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalidadDto> buscarPorId(@PathVariable Long id) {
        return localidadService.buscarPorId(id)
                .map(LocalidadDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearLocalidad(@RequestBody LocalidadDto localidadDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(LocalidadDto.fromEntity(localidadService.crearLocalidad(localidadDto.toEntity())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarLocalidad(@PathVariable Long id, @RequestBody LocalidadDto localidadDto) {
        try {
            return ResponseEntity.ok(LocalidadDto.fromEntity(localidadService.modificarLocalidad(id, localidadDto.toEntity())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarLocalidad(@PathVariable Long id) {
        try {
            localidadService.eliminarLocalidad(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
