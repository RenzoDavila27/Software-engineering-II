package com.car.business.mappers;

import com.car.business.domain.Imagen;
import com.car.business.dto.ImagenDto;
import org.springframework.stereotype.Component;

@Component
public class ImagenMapper implements BaseMapper<Imagen, ImagenDto, String> {

    @Override
    public ImagenDto toDto(Imagen entity) {
        if (entity == null) {
            return null;
        }
        ImagenDto dto = new ImagenDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setMime(entity.getMime());
        dto.setContenido(entity.getContenido());
        dto.setTipoImagen(entity.getTipoImagen());
        return dto;
    }

    @Override
    public Imagen toEntity(ImagenDto dto) {
        if (dto == null) {
            return null;
        }
        Imagen entity = new Imagen();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(ImagenDto dto, Imagen entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setMime(dto.getMime());
        entity.setContenido(dto.getContenido());
        entity.setTipoImagen(dto.getTipoImagen());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
