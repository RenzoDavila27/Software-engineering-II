package com.car.business.mappers;

import com.car.business.domain.CostoVehiculo;
import com.car.business.dto.CostoVehiculoDto;
import org.springframework.stereotype.Component;

@Component
public class CostoVehiculoMapper implements BaseMapper<CostoVehiculo, CostoVehiculoDto, String> {

    private final CaracteristicaVehiculoMapper caracteristicaVehiculoMapper;

    public CostoVehiculoMapper(CaracteristicaVehiculoMapper caracteristicaVehiculoMapper){
        this.caracteristicaVehiculoMapper = caracteristicaVehiculoMapper;
    }

    @Override
    public CostoVehiculoDto toDto(CostoVehiculo entity) {
        if (entity == null) {
            return null;
        }
        CostoVehiculoDto dto = new CostoVehiculoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setFechaDesde(entity.getFechaDesde());
        dto.setFechaHasta(entity.getFechaHasta());
        dto.setCosto(entity.getCosto());
        dto.setCaracteristicaVehiculoDto(caracteristicaVehiculoMapper.toDto(entity.getCaracteristicaVehiculo()));
        return dto;
    }

    @Override
    public CostoVehiculo toEntity(CostoVehiculoDto dto) {
        if (dto == null) {
            return null;
        }
        CostoVehiculo entity = new CostoVehiculo();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(CostoVehiculoDto dto, CostoVehiculo entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setFechaDesde(dto.getFechaDesde());
        entity.setFechaHasta(dto.getFechaHasta());
        entity.setCosto(dto.getCosto());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setCaracteristicaVehiculo(caracteristicaVehiculoMapper.toEntity(dto.getCaracteristicaVehiculoDto()));
    }
}
