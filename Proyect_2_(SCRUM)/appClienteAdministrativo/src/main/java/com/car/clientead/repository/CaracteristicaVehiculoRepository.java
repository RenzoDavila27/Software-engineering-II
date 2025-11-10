package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.enums.TipoImagen;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class CaracteristicaVehiculoRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/caracteristicas-vehiculo";

    public CaracteristicaVehiculoRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 🔹 Listar todas las características
    public List<CaracteristicaVehiculoDto> findAll() {
        try {
            ResponseEntity<List<CaracteristicaVehiculoDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CaracteristicaVehiculoDto>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de características del vehículo.", ex);
        }
    }

    // 🔹 Buscar por ID
    public CaracteristicaVehiculoDto findById(String id) {
        try {
            ResponseEntity<CaracteristicaVehiculoDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    CaracteristicaVehiculoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la característica del vehículo con ID: " + id, ex);
        }
    }

    // 🔹 Crear nueva característica
    public CaracteristicaVehiculoDto create(CaracteristicaVehiculoDto dto) {
        try {
            // 🔹 Si no hay imagen, crear una imagen vacía por defecto
            if (dto.getImagenDto() == null) {
                ImagenDto imagen = new ImagenDto();
                imagen.setNombre("placeholder.png");
                imagen.setMime("image/png");
                imagen.setContenido(new byte[0]); // array vacío
                imagen.setTipoImagen(TipoImagen.VEHICULO); // enum VEHICULO
                dto.setImagenDto(imagen);
            }

            ResponseEntity<CaracteristicaVehiculoDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    CaracteristicaVehiculoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear la característica del vehículo.", ex);
        }
    }

    // 🔹 Actualizar característica existente
    public CaracteristicaVehiculoDto update(String id, CaracteristicaVehiculoDto dto) {
        try {
            HttpEntity<CaracteristicaVehiculoDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<CaracteristicaVehiculoDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    CaracteristicaVehiculoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la característica del vehículo con ID: " + id, ex);
        }
    }

    // 🔹 Eliminar característica
    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la característica del vehículo con ID: " + id, ex);
        }
    }
}

