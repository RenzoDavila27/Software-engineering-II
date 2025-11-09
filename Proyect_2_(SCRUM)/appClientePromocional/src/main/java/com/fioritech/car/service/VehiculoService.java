package com.fioritech.car.service;

import com.fioritech.car.dto.VehiculoDto;
import com.fioritech.car.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    public Flux<VehiculoDto> findAll() {
        byte[] imagen = loadImage("static/img/car-1.png");
        String imagenBase64 = null;
        if (imagen != null) {
            imagenBase64 = Base64.getEncoder().encodeToString(imagen);
        }

        VehiculoDto vehiculo1 = new VehiculoDto();
        vehiculo1.setId("1");
        vehiculo1.setMarca("Mercedes Benz");
        vehiculo1.setModelo("R3");
        vehiculo1.setAnio(2020L);
        vehiculo1.setCantidadAsientos(5);
        vehiculo1.setCantidadPuertas(4);
        vehiculo1.setCosto(99.00);
        vehiculo1.setCantidadTotalVehiculos(10);
        vehiculo1.setCantidadVehiculosAlquilados(2);
        vehiculo1.setImagen(imagen);
        vehiculo1.setImagenBase64(imagenBase64);

        VehiculoDto vehiculo2 = new VehiculoDto();
        vehiculo2.setId("2");
        vehiculo2.setMarca("Toyota");
        vehiculo2.setModelo("Corolla Cross");
        vehiculo2.setAnio(2022L);
        vehiculo2.setCantidadAsientos(5);
        vehiculo2.setCantidadPuertas(4);
        vehiculo2.setCosto(128.00);
        vehiculo2.setCantidadTotalVehiculos(15);
        vehiculo2.setCantidadVehiculosAlquilados(5);
        vehiculo2.setImagen(imagen);
        vehiculo2.setImagenBase64(imagenBase64);

        VehiculoDto vehiculo3 = new VehiculoDto();
        vehiculo3.setId("3");
        vehiculo3.setMarca("Tesla");
        vehiculo3.setModelo("Model S Plaid");
        vehiculo3.setAnio(2023L);
        vehiculo3.setCantidadAsientos(5);
        vehiculo3.setCantidadPuertas(4);
        vehiculo3.setCosto(170.00);
        vehiculo3.setCantidadTotalVehiculos(8);
        vehiculo3.setCantidadVehiculosAlquilados(1);
        vehiculo3.setImagen(imagen);
        vehiculo3.setImagenBase64(imagenBase64);

        VehiculoDto vehiculo4 = new VehiculoDto();
        vehiculo4.setId("4");
        vehiculo4.setMarca("Hyundai");
        vehiculo4.setModelo("Kona Electric");
        vehiculo4.setAnio(2021L);
        vehiculo4.setCantidadAsientos(5);
        vehiculo4.setCantidadPuertas(4);
        vehiculo4.setCosto(187.00);
        vehiculo4.setCantidadTotalVehiculos(12);
        vehiculo4.setCantidadVehiculosAlquilados(3);
        vehiculo4.setImagen(imagen);
        vehiculo4.setImagenBase64(imagenBase64);

        return Flux.just(vehiculo1, vehiculo2, vehiculo3, vehiculo4);
        // return vehiculoRepository.findAll().map(this::validate);
    }

    public Mono<VehiculoDto> findById(String id) {
        return findAll().filter(v -> v.getId().equals(id)).next();
    }

    private VehiculoDto validate(VehiculoDto vehiculoDto) {
        // TODO: Add validation logic
        return vehiculoDto;
    }

    private byte[] loadImage(String path) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                return null;
            }
            return StreamUtils.copyToByteArray(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
