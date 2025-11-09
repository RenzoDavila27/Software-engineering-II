package com.fioritech.car.mapper;

import com.fioritech.car.dto.AlquilerDto;
import com.fioritech.car.model.Alquiler;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlquilerMapper {
    AlquilerDto toDto(Alquiler alquiler);
    Alquiler toEntity(AlquilerDto alquilerDto);
}
