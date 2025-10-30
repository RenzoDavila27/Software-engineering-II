package com.contactos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.ContactoQueryService;
import com.contactos.business.logic.service.EmpresaService;
import com.contactos.business.logic.service.PersonaService;

@RestController
@RequestMapping("/api/personas")
public class PersonaController extends BaseController<Persona, Long> {

    private final PersonaService personaService;
    private final EmpresaService empresaService;
    private final ContactoQueryService contactoQueryService;

    public PersonaController(PersonaService personaService,
                             EmpresaService empresaService,
                             ContactoQueryService contactoQueryService) {
        super(personaService);
        this.personaService = personaService;
        this.empresaService = empresaService;
        this.contactoQueryService = contactoQueryService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<Persona>> listarPersonas() throws ErrorServiceException {
        return ResponseEntity.ok(personaService.listarActivasConRelaciones());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersona(@PathVariable Long id) throws ErrorServiceException {
        return personaService.obtenerConRelaciones(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}/empresas")
    public ResponseEntity<List<Empresa>> obtenerEmpresas(@PathVariable Long id) throws ErrorServiceException {
        Persona persona = personaService.obtenerEntidad(id);
        return ResponseEntity.ok(empresaService.buscarPorPersona(persona));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}/contactos")
    public ResponseEntity<List<Contacto>> obtenerContactos(@PathVariable Long id) throws ErrorServiceException {
        return ResponseEntity.ok(contactoQueryService.listarPorPersona(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Persona> crearPersona(@RequestBody Persona persona) throws ErrorServiceException {
        return create(persona, "/api/personas");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizarPersona(@PathVariable Long id, @RequestBody Persona persona)
            throws ErrorServiceException {
        return update(id, persona);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersona(@PathVariable Long id) throws ErrorServiceException {
        return delete(id);
    }
}
