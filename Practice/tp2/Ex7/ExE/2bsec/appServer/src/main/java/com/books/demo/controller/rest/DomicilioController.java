package com.books.demo.controller.rest;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.logic.service.DomicilioService;
import com.books.demo.bussiness.logic.service.LocalidadService;
import com.books.demo.controller.rest.dto.DomicilioDto;
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
@RequestMapping("/api/domicilios")
public class DomicilioController {

    private final DomicilioService domicilioService;
    private final LocalidadService localidadService;

    
    public DomicilioController(DomicilioService domicilioService,
                               LocalidadService localidadService) {
        this.domicilioService = domicilioService;
        this.localidadService = localidadService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DomicilioDto>> listarDomiciliosActivos() {
        List<DomicilioDto> domicilios = domicilioService.listarActivos()
                .stream()
                .map(DomicilioDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(domicilios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DomicilioDto> buscarPorId(@PathVariable Long id) {
        return domicilioService.buscarPorId(id)
                .map(DomicilioDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearDomicilio(@RequestBody DomicilioDto domicilioDto) {
        try {
            Localidad localidad = obtenerLocalidad(domicilioDto.getLocalidadId());
            Domicilio domicilio = new Domicilio();
            domicilio.setCalle(domicilioDto.getCalle());
            domicilio.setNumero(domicilioDto.getNumero());
            domicilio.setLocalidad(localidad);
            domicilio.setEliminado(domicilioDto.isEliminado());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DomicilioDto.fromEntity(domicilioService.crearDomicilio(domicilio)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarDomicilio(@PathVariable Long id, @RequestBody DomicilioDto domicilioDto) {
        try {
            Localidad localidad = obtenerLocalidad(domicilioDto.getLocalidadId());
            Domicilio datos = new Domicilio();
            datos.setCalle(domicilioDto.getCalle());
            datos.setNumero(domicilioDto.getNumero());
            datos.setLocalidad(localidad);
            datos.setEliminado(domicilioDto.isEliminado());
            return ResponseEntity.ok(DomicilioDto.fromEntity(domicilioService.modificarDomicilio(id, datos)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarDomicilio(@PathVariable Long id) {
        try {
            domicilioService.eliminarDomicilio(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Localidad obtenerLocalidad(Long localidadId) {
        if (localidadId == null) {
            throw new IllegalArgumentException("Debe indicar la localidad del domicilio");
        }
        return localidadService.buscarPorId(localidadId)
                .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada con id " + localidadId));
    }
}
