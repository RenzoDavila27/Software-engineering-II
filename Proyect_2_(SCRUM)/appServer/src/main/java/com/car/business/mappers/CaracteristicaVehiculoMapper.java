package com.car.business.mappers;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.domain.CostoVehiculo;
import com.car.business.domain.Imagen;
import com.car.business.dto.CaracteristicaVehiculoDto;
import com.car.business.dto.CostoVehiculoDto;
import com.car.business.percistence.repository.CostoVehiculoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CaracteristicaVehiculoMapper implements BaseMapper<CaracteristicaVehiculo, CaracteristicaVehiculoDto, String> {

    private final EntityReferenceResolver resolver;
    private final ImagenMapper imagenMapper;
    private final CostoVehiculoRepository costoVehiculoRepository;
    private final CostoVehiculoMapper costoVehiculoMapper;

    public CaracteristicaVehiculoMapper(EntityReferenceResolver resolver, ImagenMapper imagenMapper, CostoVehiculoRepository costoVehiculoRepository, CostoVehiculoMapper costoVehiculoMapper) {
        this.resolver = resolver;
        this.imagenMapper = imagenMapper;
        this.costoVehiculoRepository = costoVehiculoRepository;
        this.costoVehiculoMapper = costoVehiculoMapper;
    }

    @Override
    public CaracteristicaVehiculoDto toDto(CaracteristicaVehiculo entity) {
        if (entity == null) {
            return null;
        }
        CaracteristicaVehiculoDto dto = new CaracteristicaVehiculoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setMarca(entity.getMarca());
        dto.setModelo(entity.getModelo());
        dto.setAnio(entity.getAnio());
        dto.setCantidadAsientos(entity.getCantidadAsientos());
        dto.setCantidadPuertas(entity.getCantidadPuertas());
        dto.setCantidadTotalVehiculos(entity.getCantidadTotalVehiculos());
        dto.setCantidadTotalVehiculosAlquilados(entity.getCantidadTotalVehiculosAlquilados());
        dto.setImagenDto(imagenMapper.toDto(entity.getImagen()));

        // Fetch and map associated CostoVehiculo entities
        List<CostoVehiculo> costos = costoVehiculoRepository.listarCostosPorVehiculo(entity.getId());
        dto.setCostosVehiculo(costos.stream()
                .map(costoVehiculoMapper::toDto)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public CaracteristicaVehiculo toEntity(CaracteristicaVehiculoDto dto) {
        if (dto == null) {
            return null;
        }
        CaracteristicaVehiculo entity = new CaracteristicaVehiculo();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(CaracteristicaVehiculoDto dto, CaracteristicaVehiculo entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setAnio(dto.getAnio());
        entity.setCantidadAsientos(dto.getCantidadAsientos());
        entity.setCantidadPuertas(dto.getCantidadPuertas());
        entity.setCantidadTotalVehiculos(dto.getCantidadTotalVehiculos());
        entity.setCantidadTotalVehiculosAlquilados(dto.getCantidadTotalVehiculosAlquilados());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        Imagen imagen = imagenMapper.toEntity(dto.getImagenDto());
        entity.setImagen(imagen);

        // Handle CostoVehiculo updates
        if (dto.getCostosVehiculo() != null) {
            for (CostoVehiculoDto costoDto : dto.getCostosVehiculo()) {
                CostoVehiculo costo = costoVehiculoMapper.toEntity(costoDto);
                costo.setCaracteristicaVehiculo(entity); // Set the bidirectional relationship
                costoVehiculoRepository.save(costo); // Save or update the CostoVehiculo
            }
        }
    }
}
