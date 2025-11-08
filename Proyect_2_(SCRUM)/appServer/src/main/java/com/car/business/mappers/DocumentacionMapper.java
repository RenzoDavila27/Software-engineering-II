package com.car.business.mappers;

import com.car.business.domain.Documentacion;
import com.car.business.dto.DocumentacionDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentacionMapper implements BaseMapper<Documentacion, DocumentacionDto, String> {

    @Override
    public DocumentacionDto toDto(Documentacion entity) {
        if (entity == null) {
            return null;
        }
        DocumentacionDto dto = new DocumentacionDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setTipoDocumentacion(entity.getTipoDocumentacion());
        dto.setObservacion(entity.getObservacion());
        dto.setPathArchivo(entity.getPathArchivo());
        dto.setNombreArchivo(entity.getNombreArchivo());
        return dto;
    }

    @Override
    public Documentacion toEntity(DocumentacionDto dto) {
        if (dto == null) {
            return null;
        }
        Documentacion entity = new Documentacion();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(DocumentacionDto dto, Documentacion entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setTipoDocumentacion(dto.getTipoDocumentacion());
        entity.setObservacion(dto.getObservacion());
        entity.setPathArchivo(dto.getPathArchivo());
        entity.setNombreArchivo(dto.getNombreArchivo());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
