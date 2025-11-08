package com.car.business.mappers;

import com.car.business.domain.Contacto;
import com.car.business.domain.Empresa;
import com.car.business.domain.Persona;
import com.car.business.dto.EmpresaDto;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper implements BaseMapper<Empresa, EmpresaDto, String> {

    private final EntityReferenceResolver resolver;

    public EmpresaMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public EmpresaDto toDto(Empresa entity) {
        if (entity == null) {
            return null;
        }
        EmpresaDto dto = new EmpresaDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setPersonaId(entity.getPersona() != null ? entity.getPersona().getId() : null);
        dto.setContactoId(entity.getContacto() != null ? entity.getContacto().getId() : null);
        return dto;
    }

    @Override
    public Empresa toEntity(EmpresaDto dto) {
        if (dto == null) {
            return null;
        }
        Empresa entity = new Empresa();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(EmpresaDto dto, Empresa entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setPersona(resolver.getReference(Persona.class, dto.getPersonaId()));
        entity.setContacto(resolver.getReference(Contacto.class, dto.getContactoId()));
    }
}
