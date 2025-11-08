package com.car.business.mappers;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.domain.Vehiculo;
import com.car.business.dto.VehiculoDto;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper implements BaseMapper<Vehiculo, VehiculoDto, String> {

    private final EntityReferenceResolver resolver;

    public VehiculoMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public VehiculoDto toDto(Vehiculo entity) {
        if (entity == null) {
            return null;
        }
        VehiculoDto dto = new VehiculoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setEstadoVehiculo(entity.getEstadoVehiculo());
        dto.setPatente(entity.getPatente());
        dto.setCaracteristicaVehiculoId(entity.getCaracteristicaVehiculo() != null ? entity.getCaracteristicaVehiculo().getId() : null);
        return dto;
    }

    @Override
    public Vehiculo toEntity(VehiculoDto dto) {
        if (dto == null) {
            return null;
        }
        Vehiculo entity = new Vehiculo();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(VehiculoDto dto, Vehiculo entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setEstadoVehiculo(dto.getEstadoVehiculo());
        entity.setPatente(dto.getPatente());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setCaracteristicaVehiculo(resolver.getReference(CaracteristicaVehiculo.class, dto.getCaracteristicaVehiculoId()));
    }
}
