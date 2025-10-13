package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.ArchivoLibro;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.persistance.ArchivoLibroRepository;
import com.books.demo.bussiness.persistance.LibroRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArchivoLibroService {

    private static final Path DIRECTORIO_BIBLIOTECA = Paths.get("/home/renzo/biblioteca");

    private final ArchivoLibroRepository archivoLibroRepository;
    private final LibroRepository libroRepository;

    public ArchivoLibroService(ArchivoLibroRepository archivoLibroRepository,
                               LibroRepository libroRepository) {
        this.archivoLibroRepository = archivoLibroRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public ArchivoLibro guardarArchivo(Long libroId, MultipartFile archivo) {
        if (libroId == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio");
        }
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar un archivo PDF");
        }

        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !tipoContenido.toLowerCase().contains("pdf")) {
            throw new IllegalArgumentException("Solo se aceptan archivos PDF");
        }

        Libro libro = libroRepository.buscarPorId(libroId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con id " + libroId));

        ArchivoLibro archivoLibro = archivoLibroRepository.findByLibroId(libroId)
                .orElseGet(ArchivoLibro::new);
        archivoLibro.setLibro(libro);
        String nombreFinal = construirNombreArchivo(libro);
        archivoLibro.setNombreArchivo(nombreFinal);
        archivoLibro.setTipoContenido(tipoContenido);
        Path rutaDestino = guardarEnSistemaDeArchivos(archivo, nombreFinal);
        archivoLibro.setRutaArchivo(rutaDestino.toString());
        archivoLibro.setTamano(tamanoArchivo(rutaDestino));
        archivoLibro.setFechaSubida(LocalDateTime.now());

        libro.setArchivoLibro(archivoLibro);
        return archivoLibroRepository.save(archivoLibro);
    }

    @Transactional(readOnly = true)
    public Optional<ArchivoLibro> obtenerPorLibroId(Long libroId) {
        if (libroId == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio");
        }
        return archivoLibroRepository.findByLibroId(libroId);
    }

    @Transactional
    public void eliminarPorLibroId(Long libroId) {
        if (libroId == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio");
        }
        archivoLibroRepository.findByLibroId(libroId).ifPresent(archivoLibro -> {
            Libro libro = archivoLibro.getLibro();
            if (libro != null) {
                libro.setArchivoLibro(null);
            }
            eliminarArchivoFisico(archivoLibro.getRutaArchivo());
            archivoLibroRepository.delete(archivoLibro);
        });
    }

    @Transactional(readOnly = true)
    public byte[] cargarDatos(ArchivoLibro archivoLibro) {
        if (archivoLibro == null || !StringUtils.hasText(archivoLibro.getRutaArchivo())) {
            throw new IllegalArgumentException("El archivo no tiene ruta asociada");
        }
        Path ruta = Paths.get(archivoLibro.getRutaArchivo());
        try {
            return Files.readAllBytes(ruta);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo PDF almacenado", e);
        }
    }

    private Path guardarEnSistemaDeArchivos(MultipartFile archivo, String nombreFinal) {
        try {
            Files.createDirectories(DIRECTORIO_BIBLIOTECA);
            Path destino = DIRECTORIO_BIBLIOTECA.resolve(nombreFinal);
            try (InputStream inputStream = archivo.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            return destino;
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo guardar el archivo PDF en disco", e);
        }
    }

    private long tamanoArchivo(Path rutaDestino) {
        try {
            return Files.size(rutaDestino);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo determinar el tamaño del PDF almacenado", e);
        }
    }

    private void eliminarArchivoFisico(String rutaArchivo) {
        if (!StringUtils.hasText(rutaArchivo)) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(rutaArchivo));
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo eliminar el archivo PDF almacenado", e);
        }
    }

    private String construirNombreArchivo(Libro libro) {
        String baseTitulo = slugTitulo(libro != null ? libro.getTitulo() : null);
        return baseTitulo + "_.pdf";
    }

    private String slugTitulo(String titulo) {
        if (!StringUtils.hasText(titulo)) {
            return "libro";
        }
        String normalizado = titulo.trim().toLowerCase();
        normalizado = normalizado.replaceAll("[^a-z0-9]+", "_");
        normalizado = normalizado.replaceAll("_+", "_");
        normalizado = normalizado.replaceAll("^_|_$", "");
        if (!StringUtils.hasText(normalizado)) {
            normalizado = "libro";
        }
        return normalizado;
    }
}
