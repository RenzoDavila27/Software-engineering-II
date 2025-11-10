package com.car.business.mappers;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.domain.CostoVehiculo;
import com.car.business.domain.Imagen;
import com.car.business.dto.CaracteristicaVehiculoDto;
import org.springframework.stereotype.Component;

@Component
public class CaracteristicaVehiculoMapper implements BaseMapper<CaracteristicaVehiculo, CaracteristicaVehiculoDto, String> {

    private final EntityReferenceResolver resolver;
    private final ImagenMapper imagenMapper;


    public CaracteristicaVehiculoMapper(EntityReferenceResolver resolver, ImagenMapper imagenMapper) {
        this.resolver = resolver;
        this.imagenMapper = imagenMapper;
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
    }
}
