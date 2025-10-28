package com.contactos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.contactos.business.domain.BaseEntity;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.BaseService;

public abstract class BaseController<T extends BaseEntity<ID>, ID> {

    protected final BaseService<T, ID> service;

    protected BaseController(BaseService<T, ID> service) {
        this.service = service;
    }

    protected ResponseEntity<List<T>> listAll() throws ErrorServiceException {
        return ResponseEntity.ok(service.listarActivos());
    }

    protected ResponseEntity<T> getOne(ID id) throws ErrorServiceException {
        return service.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    protected ResponseEntity<T> create(T entidad, String location) throws ErrorServiceException {
        T creado = service.alta(entidad);
        return ResponseEntity.created(URI.create(location + "/" + creado.getId())).body(creado);
    }

    protected ResponseEntity<T> update(ID id, T entidad) throws ErrorServiceException {
        return service.modificar(id, entidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    protected ResponseEntity<Void> delete(ID id) throws ErrorServiceException {
        service.baja(id);
        return ResponseEntity.noContent().build();
    }
}
