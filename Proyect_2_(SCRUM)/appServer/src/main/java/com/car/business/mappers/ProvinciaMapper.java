package com.car.business.mappers;

import com.car.business.domain.Pais;
import com.car.business.domain.Provincia;
import com.car.business.dto.ProvinciaDto;
import org.springframework.stereotype.Component;

@Component
public class ProvinciaMapper implements BaseMapper<Provincia, ProvinciaDto, String> {

    private final EntityReferenceResolver resolver;

    public ProvinciaMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public ProvinciaDto toDto(Provincia entity) {
        if (entity == null) {
            return null;
        }
        ProvinciaDto dto = new ProvinciaDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setPaisId(entity.getPais() != null ? entity.getPais().getId() : null);
        dto.setDepartamentoIds(MapperUtils.toIds(entity.getDepartamentos()));
        return dto;
    }

    @Override
    public Provincia toEntity(ProvinciaDto dto) {
        if (dto == null) {
            return null;
        }
        Provincia entity = new Provincia();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(ProvinciaDto dto, Provincia entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setPais(resolver.getReference(Pais.class, dto.getPaisId()));
    }
}
