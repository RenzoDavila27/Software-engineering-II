package com.car.business.mappers;

import com.car.business.domain.Direccion;
import com.car.business.domain.Localidad;
import com.car.business.dto.DireccionDto;
import org.springframework.stereotype.Component;

@Component
public class DireccionMapper implements BaseMapper<Direccion, DireccionDto, String> {

    private final EntityReferenceResolver resolver;

    public DireccionMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public DireccionDto toDto(Direccion entity) {
        if (entity == null) {
            return null;
        }
        DireccionDto dto = new DireccionDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setCalle(entity.getCalle());
        dto.setNumeracion(entity.getNumeracion());
        dto.setBarrio(entity.getBarrio());
        dto.setManzanaPiso(entity.getManzanaPiso());
        dto.setCasaDepartamento(entity.getCasaDepartamento());
        dto.setReferencia(entity.getReferencia());
        dto.setLocalidadId(entity.getLocalidad() != null ? entity.getLocalidad().getId() : null);
        return dto;
    }

    @Override
    public Direccion toEntity(DireccionDto dto) {
        if (dto == null) {
            return null;
        }
        Direccion entity = new Direccion();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(DireccionDto dto, Direccion entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setCalle(dto.getCalle());
        entity.setNumeracion(dto.getNumeracion());
        entity.setBarrio(dto.getBarrio());
        entity.setManzanaPiso(dto.getManzanaPiso());
        entity.setCasaDepartamento(dto.getCasaDepartamento());
        entity.setReferencia(dto.getReferencia());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setLocalidad(resolver.getReference(Localidad.class, dto.getLocalidadId()));
    }
}
