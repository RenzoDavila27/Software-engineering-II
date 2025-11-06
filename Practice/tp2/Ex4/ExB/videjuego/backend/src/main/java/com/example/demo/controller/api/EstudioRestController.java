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

import com.example.demo.business.domain.Estudio;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.EstudioService;

@RestController
@RequestMapping("api/v1/estudio")
public class EstudioRestController {

    private static final String SUCCESS_MESSAGE = "La acción se realizó correctamente";
    private static final String ERROR_MESSAGE = "Error al procesar la petición";

    private final EstudioService estudioService;

    public EstudioRestController(EstudioService estudioService) {
        this.estudioService = estudioService;
    }

    @GetMapping("/listarEstudio")
    public ResponseEntity<?> listarEstudio() {
        try {
            List<Estudio> estudios = estudioService.listarActivos();
            return ResponseEntity.status(HttpStatus.OK).body(estudios);
        } catch (Exception e) {
            return error(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEstudio(@PathVariable Long id) {
        try {
            Estudio estudio = estudioService.obtenerActivo(id);
            return ResponseEntity.status(HttpStatus.OK).body(estudio);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearEstudio(@RequestBody Estudio estudio) {
        try {
            estudioService.crear(estudio);
            return success(HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarEstudio(@PathVariable Long id, @RequestBody Estudio estudio) {
        try {
            estudioService.actualizar(id, estudio);
            return success(HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEstudio(@PathVariable Long id) {
        try {
            estudioService.eliminar(id);
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
