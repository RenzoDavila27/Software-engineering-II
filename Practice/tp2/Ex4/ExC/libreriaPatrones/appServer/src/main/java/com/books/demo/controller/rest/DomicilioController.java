package com.books.demo.controller.rest;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.DomicilioService;
import com.books.demo.controller.rest.dto.DomicilioDto;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/api/domicilios")
public class DomicilioController {

    private final DomicilioService domicilioService;

    public DomicilioController(DomicilioService domicilioService) {
        this.domicilioService = domicilioService;
    }

    @GetMapping
    public ResponseEntity<?> listarDomiciliosActivos() {
        try {
            List<DomicilioDto> domicilios = domicilioService.listarActivos()
                    .stream()
                    .map(DomicilioDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(domicilios);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar domicilios.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return domicilioService.buscarPorId(id)
                    .map(DomicilioDto::fromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearDomicilio(@RequestBody DomicilioDto domicilioDto) {
        try {
            Domicilio domicilio = construirDomicilioDesdeDto(domicilioDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DomicilioDto.fromEntity(domicilioService.crearDomicilio(domicilio)));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDomicilio(@PathVariable Long id, @RequestBody DomicilioDto domicilioDto) {
        try {
            Domicilio datos = construirDomicilioDesdeDto(domicilioDto);
            return ResponseEntity.ok(DomicilioDto.fromEntity(domicilioService.modificarDomicilio(id, datos)));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDomicilio(@PathVariable Long id) {
        try {
            domicilioService.eliminarDomicilio(id);
            return ResponseEntity.noContent().build();
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Domicilio construirDomicilioDesdeDto(DomicilioDto dto) {
        Localidad localidad = new Localidad();
        localidad.setId(dto.getLocalidadId());

        Domicilio domicilio = new Domicilio();
        domicilio.setCalle(dto.getCalle());
        domicilio.setNumero(dto.getNumero());
        domicilio.setLocalidad(localidad);
        domicilio.setEliminado(dto.isEliminado());
        return domicilio;
    }
}
