package com.car.clientead.business.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.car.clientead.client.dto.DocumentacionDto;
import com.car.clientead.client.dto.enums.TipoDocumentacion;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.DocumentacionRepository;

@Service
public class DocumentacionService {

    private static final List<String> EXTENSIONES_PERMITIDAS = List.of("pdf", "doc", "docx");

    @Value("${documentacion.base-path:/home/f4cul3ll4/documentacion}")
    private String basePath;

    @Autowired
    private DocumentacionRepository repository;

    public DocumentacionDto registrar(TipoDocumentacion tipo, String observacion, List<MultipartFile> archivos) {
        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de documentación.");
        }
        if (archivos == null || archivos.isEmpty() || archivos.stream().allMatch(MultipartFile::isEmpty)) {
            throw new IllegalArgumentException("Debe adjuntar al menos un archivo (PDF o Word).");
        }
        try {
            Path carpeta = Paths.get(basePath).toAbsolutePath().normalize();
            Files.createDirectories(carpeta);
            String archivoZip = generarNombreArchivo();
            Path destino = carpeta.resolve(archivoZip);
            empaquetarArchivos(destino, archivos);

            DocumentacionDto dto = new DocumentacionDto();
            dto.setTipoDocumentacion(tipo);
            dto.setObservacion(StringUtils.hasText(observacion) ? observacion : "");
            dto.setPathArchivo(carpeta.toString());
            dto.setNombreArchivo(archivoZip);
            return repository.create(dto);
        } catch (IOException ex) {
            throw new ApiClientException("No se pudo almacenar la documentación.", ex);
        }
    }

    public DocumentacionDto consultar(String id) {
        return repository.findById(id);
    }

    public Resource descargarRecurso(String id) {
        DocumentacionDto dto = consultar(id);
        if (dto == null || !StringUtils.hasText(dto.getPathArchivo()) || !StringUtils.hasText(dto.getNombreArchivo())) {
            throw new ApiClientException("La documentación solicitada no está disponible.");
        }
        try {
            Path archivo = Paths.get(dto.getPathArchivo()).resolve(dto.getNombreArchivo()).normalize();
            if (!Files.exists(archivo)) {
                throw new ApiClientException("El archivo físico no existe en el servidor.");
            }
            return new UrlResource(archivo.toUri());
        } catch (IOException ex) {
            throw new ApiClientException("No se pudo acceder al archivo de documentación.", ex);
        }
    }

    public void eliminar(String id) {
        DocumentacionDto dto = consultar(id);
        if (dto != null && StringUtils.hasText(dto.getPathArchivo()) && StringUtils.hasText(dto.getNombreArchivo())) {
            Path archivo = Paths.get(dto.getPathArchivo()).resolve(dto.getNombreArchivo()).normalize();
            try {
                Files.deleteIfExists(archivo);
            } catch (IOException ignored) {
            }
        }
        repository.delete(id);
    }

    private void empaquetarArchivos(Path destinoZip, List<MultipartFile> archivos) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(destinoZip))) {
            for (MultipartFile archivo : archivos) {
                if (archivo == null || archivo.isEmpty()) {
                    continue;
                }
                String nombre = limpiarNombre(Objects.requireNonNull(archivo.getOriginalFilename()));
                String extension = obtenerExtension(nombre);
                if (!EXTENSIONES_PERMITIDAS.contains(extension.toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("El archivo " + nombre + " no posee un formato permitido.");
                }
                ZipEntry entry = new ZipEntry(nombre);
                zos.putNextEntry(entry);
                try (var in = archivo.getInputStream()) {
                    in.transferTo(zos);
                }
                zos.closeEntry();
            }
        }
    }

    private String generarNombreArchivo() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        return "documentacion-" + timestamp + "-" + UUID.randomUUID().toString().substring(0, 8) + ".zip";
    }

    private String limpiarNombre(String original) {
        String nombre = original != null ? original : "archivo";
        nombre = nombre.replaceAll("[^A-Za-z0-9._-]", "_");
        return nombre;
    }

    private String obtenerExtension(String nombre) {
        int idx = nombre.lastIndexOf('.');
        if (idx == -1) {
            return "";
        }
        return nombre.substring(idx + 1);
    }
}
