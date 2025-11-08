package com.car.business.mappers;

import com.car.business.domain.Departamento;
import com.car.business.domain.Provincia;
import com.car.business.dto.DepartamentoDto;
import org.springframework.stereotype.Component;

@Component
public class DepartamentoMapper implements BaseMapper<Departamento, DepartamentoDto, String> {

    private final EntityReferenceResolver resolver;

    public DepartamentoMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public DepartamentoDto toDto(Departamento entity) {
        if (entity == null) {
            return null;
        }
        DepartamentoDto dto = new DepartamentoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setProvinciaId(entity.getProvincia() != null ? entity.getProvincia().getId() : null);
        dto.setLocalidadIds(MapperUtils.toIds(entity.getLocalidades()));
        return dto;
    }

    @Override
    public Departamento toEntity(DepartamentoDto dto) {
        if (dto == null) {
            return null;
        }
        Departamento entity = new Departamento();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(DepartamentoDto dto, Departamento entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setProvincia(resolver.getReference(Provincia.class, dto.getProvinciaId()));
    }
}
