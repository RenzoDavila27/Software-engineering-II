package com.fioritech.car.mapper;

import com.fioritech.car.dto.VehiculoDto;
import com.fioritech.car.model.Vehiculo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehiculoMapper {
    VehiculoDto toDto(Vehiculo vehiculo);
    Vehiculo toEntity(VehiculoDto vehiculoDto);
}
