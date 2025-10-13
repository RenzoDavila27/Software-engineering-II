package com.books.demo.controller.rest;

import com.books.demo.bussiness.domain.ArchivoLibro;
import com.books.demo.bussiness.logic.service.ArchivoLibroService;
import com.books.demo.controller.rest.dto.ArchivoLibroDto;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/libros/{libroId}/archivo")
public class ArchivoLibroController {

    private final ArchivoLibroService archivoLibroService;

    public ArchivoLibroController(ArchivoLibroService archivoLibroService) {
        this.archivoLibroService = archivoLibroService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirArchivo(@PathVariable Long libroId,
                                          @RequestParam("archivo") MultipartFile archivo) {
        try {
            ArchivoLibro guardado = archivoLibroService.guardarArchivo(libroId, archivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(ArchivoLibroDto.fromEntity(guardado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/metadata")
    public ResponseEntity<ArchivoLibroDto> obtenerMetadata(@PathVariable Long libroId) {
        Optional<ArchivoLibro> archivo = archivoLibroService.obtenerPorLibroId(libroId);
        return archivo
                .map(ArchivoLibroDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> descargarArchivo(@PathVariable Long libroId) {
        try {
            Optional<ArchivoLibro> archivoOpt = archivoLibroService.obtenerPorLibroId(libroId);
            if (archivoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ArchivoLibro archivo = archivoOpt.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(archivo.getNombreArchivo(), StandardCharsets.UTF_8)
                    .build());
            headers.setContentType(MediaType.parseMediaType(archivo.getTipoContenido()));
            headers.setContentLength(archivo.getTamano());
            byte[] datos = archivoLibroService.cargarDatos(archivo);
            return new ResponseEntity<>(datos, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarArchivo(@PathVariable Long libroId) {
        try {
            archivoLibroService.eliminarPorLibroId(libroId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
