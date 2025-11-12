package com.car.business.mappers;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.domain.CostoVehiculo;
import com.car.business.dto.CaracteristicaVehiculoDto;
import com.car.business.dto.CostoVehiculoDto;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class CostoVehiculoMapper implements BaseMapper<CostoVehiculo, CostoVehiculoDto, String> {

    private final EntityReferenceResolver resolver;
    private static final LocalDate FECHA_POR_DEFECTO = LocalDate.of(9999, 1, 1);

    public CostoVehiculoMapper(EntityReferenceResolver resolver){
        this.resolver = resolver;
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
        dto.setFechaHasta(entity.getFechaHasta() != null ? entity.getFechaHasta() : FECHA_POR_DEFECTO);
        dto.setCosto(entity.getCosto());
        if (entity.getCaracteristicaVehiculo() != null) {
            CaracteristicaVehiculoDto caracteristicaDto = new CaracteristicaVehiculoDto();
            caracteristicaDto.setId(entity.getCaracteristicaVehiculo().getId());
            // Only map the ID to break the circular dependency in DTOs
            dto.setCaracteristicaVehiculoDto(caracteristicaDto);
        }
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
        LocalDate fechaHasta = dto.getFechaHasta() != null ? dto.getFechaHasta() : FECHA_POR_DEFECTO;
        entity.setFechaHasta(fechaHasta);
        entity.setCosto(dto.getCosto());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        if (dto.getCaracteristicaVehiculoDto() != null && dto.getCaracteristicaVehiculoDto().getId() != null) {
            entity.setCaracteristicaVehiculo(resolver.getReference(CaracteristicaVehiculo.class, dto.getCaracteristicaVehiculoDto().getId()));
        }
    }
}
