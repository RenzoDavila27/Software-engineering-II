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

import com.contactos.business.domain.Empresa;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController extends BaseController<Empresa, Long> {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        super(empresaService);
        this.empresaService = empresaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<Empresa>> listarEmpresas() throws ErrorServiceException {
        return listAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtenerEmpresa(@PathVariable Long id) throws ErrorServiceException {
        return getOne(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Empresa> crearEmpresa(@RequestBody Empresa empresa) throws ErrorServiceException {
        return create(empresa, "/api/empresas");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(@PathVariable Long id, @RequestBody Empresa empresa)
            throws ErrorServiceException {
        return update(id, empresa);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) throws ErrorServiceException {
        return delete(id);
    }
}
