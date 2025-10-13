package com.books.demo.repository;

import com.books.demo.client.dto.ArchivoLibroDto;
import com.books.demo.client.dto.LibroDto;
import com.books.demo.client.exception.ApiClientException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class LibroRepository {

    private final RestTemplate restTemplate;
    private final String librosUrl = "http://localhost:8080/api/libros";

    public LibroRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LibroDto> findAll() {
        try {
            ResponseEntity<List<LibroDto>> response = restTemplate.exchange(
                    librosUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<LibroDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de libros.", ex);
        }
    }

    public Optional<LibroDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            LibroDto libro = restTemplate.getForObject(librosUrl + "/" + id, LibroDto.class);
            return Optional.ofNullable(libro);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new ApiClientException("Error al consultar el libro con id " + id + ".", ex);
        } catch (RestClientException ex) {
            throw new ApiClientException("Error al consultar el libro con id " + id + ".", ex);
        }
    }

    public Optional<LibroDto> update(Long id, LibroDto libro) {
        HttpEntity<LibroDto> request = new HttpEntity<>(libro);
        try {
            ResponseEntity<LibroDto> response = restTemplate.exchange(
                    librosUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    LibroDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el libro con id " + id + ".", ex);
        }
    }

    public LibroDto save(LibroDto libro) {
        try {
            return restTemplate.postForObject(librosUrl, libro, LibroDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar el libro.", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(librosUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el libro con id " + id + ".", ex);
        }
    }

    public Optional<ArchivoLibroDto> obtenerArchivo(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            ArchivoLibroDto archivo = restTemplate.getForObject(
                    librosUrl + "/" + id + "/archivo/metadata",
                    ArchivoLibroDto.class);
            return Optional.ofNullable(archivo);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el archivo del libro con id " + id + ".", ex);
        }
    }

    public ArchivoLibroDto guardarArchivo(Long id, MultipartFile archivo) {
        if (id == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio.");
        }
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar un archivo PDF valido.");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> cuerpo = new LinkedMultiValueMap<>();
            cuerpo.add("archivo", construirRecursoMultipart(archivo));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(cuerpo, headers);
            ResponseEntity<ArchivoLibroDto> response = restTemplate.exchange(
                    librosUrl + "/" + id + "/archivo",
                    HttpMethod.POST,
                    requestEntity,
                    ArchivoLibroDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo guardar el archivo del libro con id " + id + ".", ex);
        }
    }

    public byte[] descargarArchivo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio.");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_PDF));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    librosUrl + "/" + id + "/archivo",
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo descargar el archivo del libro con id " + id + ".", ex);
        }
    }

    private HttpEntity<ByteArrayResource> construirRecursoMultipart(MultipartFile archivo) {
        try {
            ByteArrayResource recurso = new ByteArrayResource(archivo.getBytes()) {
                @Override
                public String getFilename() {
                    return archivo.getOriginalFilename();
                }
            };
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new HttpEntity<>(recurso, headers);
        } catch (Exception ex) {
            throw new ApiClientException("No se pudo leer el archivo PDF a enviar.", ex);
        }
    }
}
