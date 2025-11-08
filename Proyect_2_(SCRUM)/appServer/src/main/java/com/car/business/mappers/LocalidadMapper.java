package com.car.business.mappers;

import com.car.business.domain.Departamento;
import com.car.business.domain.Localidad;
import com.car.business.dto.LocalidadDto;
import org.springframework.stereotype.Component;

@Component
public class LocalidadMapper implements BaseMapper<Localidad, LocalidadDto, String> {

    private final EntityReferenceResolver resolver;

    public LocalidadMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public LocalidadDto toDto(Localidad entity) {
        if (entity == null) {
            return null;
        }
        LocalidadDto dto = new LocalidadDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setCodigoPostal(entity.getCodigoPostal());
        dto.setDepartamentoId(entity.getDepartamento() != null ? entity.getDepartamento().getId() : null);
        dto.setDireccionIds(MapperUtils.toIds(entity.getDirecciones()));
        return dto;
    }

    @Override
    public Localidad toEntity(LocalidadDto dto) {
        if (dto == null) {
            return null;
        }
        Localidad entity = new Localidad();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(LocalidadDto dto, Localidad entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setCodigoPostal(dto.getCodigoPostal());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setDepartamento(resolver.getReference(Departamento.class, dto.getDepartamentoId()));
    }
}
