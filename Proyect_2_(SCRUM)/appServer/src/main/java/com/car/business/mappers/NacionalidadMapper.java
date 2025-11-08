package com.car.business.mappers;

import com.car.business.domain.Nacionalidad;
import com.car.business.dto.NacionalidadDto;
import org.springframework.stereotype.Component;

@Component
public class NacionalidadMapper implements BaseMapper<Nacionalidad, NacionalidadDto, String> {

    @Override
    public NacionalidadDto toDto(Nacionalidad entity) {
        if (entity == null) {
            return null;
        }
        NacionalidadDto dto = new NacionalidadDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        return dto;
    }

    @Override
    public Nacionalidad toEntity(NacionalidadDto dto) {
        if (dto == null) {
            return null;
        }
        Nacionalidad entity = new Nacionalidad();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(NacionalidadDto dto, Nacionalidad entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
