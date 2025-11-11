package com.fioritech.car.bussiness.mapper;

import com.fioritech.car.bussiness.dto.AlquilerDto;
import com.fioritech.car.bussiness.model.Alquiler;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlquilerMapper {
    AlquilerDto toDto(Alquiler alquiler);
    Alquiler toEntity(AlquilerDto alquilerDto);
}
