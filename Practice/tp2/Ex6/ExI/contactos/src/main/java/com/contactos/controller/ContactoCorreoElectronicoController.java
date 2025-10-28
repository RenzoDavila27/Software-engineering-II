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

import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.ContactoCorreoElectronicoService;

@RestController
@RequestMapping("/api/contactos/correos")
public class ContactoCorreoElectronicoController extends BaseController<ContactoCorreoElectronico, Long> {

    public ContactoCorreoElectronicoController(ContactoCorreoElectronicoService service) {
        super(service);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ContactoCorreoElectronico>> listarContactos() throws ErrorServiceException {
        return listAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ContactoCorreoElectronico> obtenerContacto(@PathVariable Long id)
            throws ErrorServiceException {
        return getOne(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ContactoCorreoElectronico> crearContacto(@RequestBody ContactoCorreoElectronico contacto)
            throws ErrorServiceException {
        return create(contacto, "/api/contactos/correos");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ContactoCorreoElectronico> actualizarContacto(@PathVariable Long id,
                                                                        @RequestBody ContactoCorreoElectronico contacto)
            throws ErrorServiceException {
        return update(id, contacto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContacto(@PathVariable Long id) throws ErrorServiceException {
        return delete(id);
    }
}
