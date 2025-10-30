package com.contactos.controller;

import java.util.List;

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

import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.ContactoTelefonicoService;

@RestController
@RequestMapping("/api/contactos/telefonos")
public class ContactoTelefonicoController extends BaseController<ContactoTelefonico, Long> {

    public ContactoTelefonicoController(ContactoTelefonicoService service) {
        super(service);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ContactoTelefonico>> listarContactos() throws ErrorServiceException {
        return listAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ContactoTelefonico> obtenerContacto(@PathVariable Long id)
            throws ErrorServiceException {
        return getOne(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ContactoTelefonico> crearContacto(@RequestBody ContactoTelefonico contacto)
            throws ErrorServiceException {
        return create(contacto, "/api/contactos/telefonos");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ContactoTelefonico> actualizarContacto(@PathVariable Long id,
                                                                 @RequestBody ContactoTelefonico contacto)
            throws ErrorServiceException {
        return update(id, contacto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContacto(@PathVariable Long id) throws ErrorServiceException {
        return delete(id);
    }
}
