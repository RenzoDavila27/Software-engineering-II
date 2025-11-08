package com.car.business.mappers;

import com.car.business.domain.Persona;
import com.car.business.domain.Usuario;
import com.car.business.dto.UsuarioDto;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements BaseMapper<Usuario, UsuarioDto, String> {

    private final EntityReferenceResolver resolver;

    public UsuarioMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public UsuarioDto toDto(Usuario entity) {
        if (entity == null) {
            return null;
        }
        UsuarioDto dto = new UsuarioDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombreUsuario(entity.getNombreUsuario());
        dto.setClave(entity.getClave());
        dto.setRolUsuario(entity.getRolUsuario());
        dto.setPersonaId(entity.getPersona() != null ? entity.getPersona().getId() : null);
        return dto;
    }

    @Override
    public Usuario toEntity(UsuarioDto dto) {
        if (dto == null) {
            return null;
        }
        Usuario entity = new Usuario();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(UsuarioDto dto, Usuario entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombreUsuario(dto.getNombreUsuario());
        entity.setClave(dto.getClave());
        entity.setRolUsuario(dto.getRolUsuario());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setPersona(resolver.getReference(Persona.class, dto.getPersonaId()));
    }
}
