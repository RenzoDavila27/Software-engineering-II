package com.car.clientead.business.logic;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.enums.TipoImagen;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.repository.CaracteristicaVehiculoRepository;

@Service
public class CaracteristicaVehiculoService {

    @Autowired
    private CaracteristicaVehiculoRepository repository;

    // 🔹 Listar todas
    public List<CaracteristicaVehiculoDto> listar() {
        return repository.findAll().stream()
                .filter(this::vehiculoValido)
                .map(this::prepararImagen)
                .collect(Collectors.toList());
    }

    // 🔹 Consultar por ID
    public CaracteristicaVehiculoDto consultar(String id) {
        return prepararImagen(repository.findById(id));
    }

    // 🔹 Crear nueva característica (con imagen opcional)
    public CaracteristicaVehiculoDto crear(CaracteristicaVehiculoDto dto, MultipartFile imagenFile) {
        validar(dto);
        try {
            if (imagenFile != null && !imagenFile.isEmpty()) {
                dto.setImagenDto(convertirImagen(imagenFile));
            }
            return prepararImagen(repository.create(dto));
        } catch (IOException e) {
            throw new ApiClientException("Error al procesar la imagen del vehículo.", e);
        }
    }

    // 🔹 Modificar existente
    public CaracteristicaVehiculoDto modificar(String id, CaracteristicaVehiculoDto dto, MultipartFile imagenFile) {
        validar(dto);
        try {
            if (imagenFile != null && !imagenFile.isEmpty()) {
                dto.setImagenDto(convertirImagen(imagenFile));
            }
            return prepararImagen(repository.update(id, dto));
        } catch (IOException e) {
            throw new ApiClientException("Error al procesar la imagen del vehículo.", e);
        }
    }

    // 🔹 Eliminar
    public void eliminar(String id) {
        repository.delete(id);
    }

    // ======= Métodos auxiliares =======

    private void validar(CaracteristicaVehiculoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la característica del vehículo no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getMarca()) || !StringUtils.hasText(dto.getModelo())) {
            throw new IllegalArgumentException("La marca y el modelo del vehículo son obligatorios.");
        }
        if (dto.getAnio() == null || dto.getAnio() < 1900) {
            throw new IllegalArgumentException("El año del vehículo no es válido.");
        }
    }

    private boolean vehiculoValido(CaracteristicaVehiculoDto dto) {
        return dto != null && StringUtils.hasText(dto.getMarca());
    }

    private CaracteristicaVehiculoDto prepararImagen(CaracteristicaVehiculoDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getImagenDto() != null &&
            dto.getImagenDto().getContenido() != null &&
            dto.getImagenDto().getContenido().length > 0) {
            String mime = StringUtils.hasText(dto.getImagenDto().getMime()) ?
                    dto.getImagenDto().getMime() : "image/png";
            String base64 = Base64.getEncoder().encodeToString(dto.getImagenDto().getContenido());
            dto.setImagenDataUri("data:" + mime + ";base64," + base64);
        } else {
            dto.setImagenDataUri(null);
        }
        return dto;
    }

    // Convierte el archivo en ImagenDto
    private ImagenDto convertirImagen(MultipartFile file) throws IOException {
        ImagenDto imagenDto = new ImagenDto();
        imagenDto.setNombre(file.getOriginalFilename());
        imagenDto.setMime(file.getContentType());
        imagenDto.setContenido(file.getBytes());
        imagenDto.setTipoImagen(TipoImagen.VEHICULO);
        return imagenDto;
    }
}
