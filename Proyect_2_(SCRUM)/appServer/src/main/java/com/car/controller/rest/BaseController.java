package com.car.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.car.business.domain.BaseEntity;
import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.BaseService;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;



public abstract class BaseController<T extends BaseEntity<ID>, DTO, ID> {

    protected final BaseService<T, ID> service;
    private final Class<DTO> dtoClass;

    protected BaseController(BaseService<T, ID> service, Class<DTO> dtoClass) {
        this.service = service;
        this.dtoClass = dtoClass;
    }


    @GetMapping
    public ResponseEntity<List<DTO>> listarActivos() {
        List<DTO> lista = service.listarActivos()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // -------------------------------------
    // 🔹 Buscar por ID
    // -------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DTO> obtenerPorId(@PathVariable ID id) {
        return service.obtener(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------------------
    // 🔹 Crear
    // -------------------------------------
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DTO dto) {
        try {
            T entidad = toEntity(dto);
            T creada = service.alta(entidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(creada));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // -------------------------------------
    // 🔹 Modificar
    // -------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable ID id, @RequestBody DTO dto) {
        try {
            T entidadNueva = toEntity(dto);
            return service.modificar(id, entidadNueva)
                    .map(this::toDto)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // -------------------------------------
    // 🔹 Baja lógica
    // -------------------------------------
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

    // -------------------------------------
    // 🔹 Métodos auxiliares: conversión DTO ↔ Entity
    // -------------------------------------

    private DTO toDto(T entity) {
        try {
            Method fromEntity = dtoClass.getMethod("fromEntity", entity.getClass());
            @SuppressWarnings("unchecked")
            DTO dto = (DTO) fromEntity.invoke(null, entity);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo entidad a DTO: " + e.getMessage(), e);
        }
    }

    private T toEntity(DTO dto) {
        try {
            Method toEntity = dtoClass.getMethod("toEntity");
            @SuppressWarnings("unchecked")
            T entity = (T) toEntity.invoke(dto);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo DTO a entidad: " + e.getMessage(), e);
        }
    }
}
