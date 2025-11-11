package com.car.business.mappers;

import com.car.business.domain.Contacto;
import com.car.business.domain.Direccion;
import com.car.business.domain.Imagen;
import com.car.business.domain.Persona;
import com.car.business.dto.PersonaDto;

public abstract class AbstractPersonaMapper<E extends Persona, D extends PersonaDto> implements BaseMapper<E, D, String> {

    protected final EntityReferenceResolver resolver;

    protected AbstractPersonaMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public D toDto(E entity) {
        if (entity == null) {
            return null;
        }
        D dto = createDto();
        fillDto(entity, dto);
        return dto;
    }

    @Override
    public E toEntity(D dto) {
        if (dto == null) {
            return null;
        }
        E entity = createEntity();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(D dto, E entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setFechaNacimiento(dto.getFechaNacimiento());
        entity.setTipoDocumento(dto.getTipoDocumento());
        entity.setNumeroDocumento(dto.getNumeroDocumento());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        // entity.setContactos(resolver.getReferences(Contacto.class, dto.getContactoIds())); // Assuming PersonaDto will have getContactoIds()
        entity.setDireccion(resolver.getReference(Direccion.class, dto.getDireccionId()));
        entity.setImagen(resolver.getReference(Imagen.class, dto.getImagenId()));
    }

    protected abstract D createDto();

    protected abstract E createEntity();

    protected void fillDto(Persona entity, D dto) {
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setTipoDocumento(entity.getTipoDocumento());
        dto.setNumeroDocumento(entity.getNumeroDocumento());
        // dto.setContactoIds(MapperUtils.toIds(entity.getContactos())); // Assuming PersonaDto will have setContactoIds()
        dto.setDireccionId(entity.getDireccion() != null ? entity.getDireccion().getId() : null);
        dto.setImagenId(entity.getImagen() != null ? entity.getImagen().getId() : null);
    }
}
