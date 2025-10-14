package com.books.demo.controller.rest;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.PersonaService;
import com.books.demo.controller.rest.dto.PersonaDto;
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
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<?> listarPersonasActivas() {
        try {
            List<PersonaDto> personas = personaService.listarActivas()
                    .stream()
                    .map(PersonaDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(personas);
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar personas.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return personaService.buscarPorId(id)
                    .map(PersonaDto::fromEntity)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearPersona(@RequestBody PersonaDto personaDto) {
        try {
            Persona persona = personaDto.toEntity();
            persona.setDomicilio(crearDomicilioDesdeDto(personaDto));
            Persona creada = personaService.crearPersona(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(PersonaDto.fromEntity(creada));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable Long id, @RequestBody PersonaDto personaDto) {
        try {
            Persona datos = personaDto.toEntity();
            datos.setDomicilio(crearDomicilioDesdeDto(personaDto));
            Persona actualizada = personaService.modificarPersona(id, datos);
            return ResponseEntity.ok(PersonaDto.fromEntity(actualizada));
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPersona(@PathVariable Long id) {
        try {
            personaService.eliminarPersona(id);
            return ResponseEntity.noContent().build();
        } catch (ErrorServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Domicilio crearDomicilioDesdeDto(PersonaDto dto) {
        Domicilio domicilio = new Domicilio();
        domicilio.setId(dto.getDomicilioId());
        return domicilio;
    }
}
