package com.car.controller.rest;

import com.car.business.domain.BaseEntity;
import com.car.business.dto.BaseDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.BaseService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseController<T extends BaseEntity<ID>, D extends BaseDto<ID>, ID> {

    protected final BaseService<T, D, ID> service;

    protected BaseController(BaseService<T, D, ID> service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<D>> listarActivos() throws BusinessException {
        return ResponseEntity.ok(service.listarActivosDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> obtenerPorId(@PathVariable ID id) throws BusinessException {
        return service.obtenerDto(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody D dto) {
        try {
            D creado = service.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable ID id, @RequestBody D dto) {
        try {
            return service.actualizar(id, dto)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable ID id) {
        try {
            boolean eliminado = service.bajaLogica(id);
            if (eliminado) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
