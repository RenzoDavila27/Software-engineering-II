package com.car.business.mappers;

import com.car.business.domain.Pais;
import com.car.business.dto.PaisDto;
import org.springframework.stereotype.Component;

@Component
public class PaisMapper implements BaseMapper<Pais, PaisDto, String> {

    @Override
    public PaisDto toDto(Pais entity) {
        if (entity == null) {
            return null;
        }
        PaisDto dto = new PaisDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setProvinciaIds(MapperUtils.toIds(entity.getProvincias()));
        return dto;
    }

    @Override
    public Pais toEntity(PaisDto dto) {
        if (dto == null) {
            return null;
        }
        Pais entity = new Pais();
        entity.setId(dto.getId());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(PaisDto dto, Pais entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
