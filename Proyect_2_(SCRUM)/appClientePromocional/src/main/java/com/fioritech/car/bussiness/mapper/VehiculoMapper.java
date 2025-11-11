package com.fioritech.car.bussiness.mapper;

import com.fioritech.car.bussiness.dto.VehiculoDto;
import com.fioritech.car.bussiness.model.Vehiculo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehiculoMapper {
    VehiculoDto toDto(Vehiculo vehiculo);
    Vehiculo toEntity(VehiculoDto vehiculoDto);
}
