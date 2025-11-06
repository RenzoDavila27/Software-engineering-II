package com.example.demo.controller.api;

import java.util.List;
import java.util.Map;

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

import com.example.demo.business.domain.Categoria;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.CategoriaService;

@RestController
@RequestMapping("api/v1/categoria")
public class CategoriaRestController {

    private static final String SUCCESS_MESSAGE = "La acción se realizó correctamente";
    private static final String ERROR_MESSAGE = "Error al procesar la petición";

    private final CategoriaService categoriaService;

    public CategoriaRestController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listarCategoria")
    public ResponseEntity<?> listarCategoria() {
        try {
            List<Categoria> categorias = categoriaService.listarActivos();
            return ResponseEntity.status(HttpStatus.OK).body(categorias);
        } catch (Exception e) {
            return error(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCategoria(@PathVariable Long id) {
        try {
            Categoria categoria = categoriaService.obtenerActivo(id);
            return ResponseEntity.status(HttpStatus.OK).body(categoria);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        try {
            categoriaService.crear(categoria);
            return success(HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        try {
            categoriaService.actualizar(id, categoria);
            return success(HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminar(id);
            return success(HttpStatus.NO_CONTENT);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    private ResponseEntity<Map<String, String>> success(HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("exito", SUCCESS_MESSAGE));
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
