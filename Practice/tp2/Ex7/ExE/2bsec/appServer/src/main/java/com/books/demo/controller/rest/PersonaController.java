package com.books.demo.controller.rest;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.logic.service.DomicilioService;
import com.books.demo.bussiness.logic.service.PersonaService;
import com.books.demo.controller.rest.dto.PersonaDto;
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
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;
    private final DomicilioService domicilioService;

    public PersonaController(PersonaService personaService,
                             DomicilioService domicilioService) {
        this.personaService = personaService;
        this.domicilioService = domicilioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonaDto>> listarPersonasActivas() {
        List<PersonaDto> personas = personaService.listarActivas()
                .stream()
                .map(PersonaDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonaDto> buscarPorId(@PathVariable Long id) {
        return personaService.buscarPorId(id)
                .map(PersonaDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearPersona(@RequestBody PersonaDto personaDto) {
        try {
            Domicilio domicilio = obtenerDomicilio(personaDto.getDomicilioId());
            Persona persona = personaDto.toEntity();
            persona.setDomicilio(domicilio);
            Persona creada = personaService.crearPersona(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(PersonaDto.fromEntity(creada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarPersona(@PathVariable Long id, @RequestBody PersonaDto personaDto) {
        try {
            Domicilio domicilio = obtenerDomicilio(personaDto.getDomicilioId());
            Persona datos = personaDto.toEntity();
            datos.setDomicilio(domicilio);
            Persona actualizada = personaService.modificarPersona(id, datos);
            return ResponseEntity.ok(PersonaDto.fromEntity(actualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarPersona(@PathVariable Long id) {
        try {
            personaService.eliminarPersona(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Domicilio obtenerDomicilio(Long domicilioId) {
        if (domicilioId == null) {
            throw new IllegalArgumentException("Debe indicar el domicilio de la persona");
        }
        return domicilioService.buscarPorId(domicilioId)
                .orElseThrow(() -> new IllegalArgumentException("Domicilio no encontrado con id " + domicilioId));
    }
}
