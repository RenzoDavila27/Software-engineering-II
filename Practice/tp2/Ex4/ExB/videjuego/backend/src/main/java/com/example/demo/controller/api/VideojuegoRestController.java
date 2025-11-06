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

import com.example.demo.business.domain.Videojuego;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.logic.service.VideojuegoService;
import com.example.demo.controller.api.dto.VideojuegoRequest;

@RestController
@RequestMapping("api/v1/videojuego")
public class VideojuegoRestController {

    private static final String SUCCESS_MESSAGE = "La acción se realizó correctamente";
    private static final String ERROR_MESSAGE = "Error al procesar la petición";

    private final VideojuegoService videojuegoService;

    public VideojuegoRestController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    @GetMapping("/listarVideojuego")
    public ResponseEntity<?> listarVideojuego() {
        try {
            List<Videojuego> videojuegos = videojuegoService.listarActivos();
            return ResponseEntity.status(HttpStatus.OK).body(videojuegos);
        } catch (Exception e) {
            return error(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarVideojuego(@PathVariable Long id) {
        try {
            Videojuego videojuego = videojuegoService.obtenerActivo(id);
            return ResponseEntity.status(HttpStatus.OK).body(videojuego);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearVideojuego(@RequestBody VideojuegoRequest request) {
        try {
            videojuegoService.crearDesdeRequest(request);
            return success(HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarVideojuego(@PathVariable Long id, @RequestBody VideojuegoRequest request) {
        try {
            videojuegoService.actualizarDesdeRequest(id, request);
            return success(HttpStatus.OK);
        } catch (ErrorServiceException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.BAD_REQUEST, ERROR_MESSAGE);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVideojuego(@PathVariable Long id) {
        try {
            videojuegoService.eliminar(id);
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
