package com.car.business.mappers;

import com.car.business.domain.ContactoTelefonico;
import com.car.business.dto.ContactoTelefonicoDto;
import org.springframework.stereotype.Component;

@Component
public class ContactoTelefonicoMapper implements BaseMapper<ContactoTelefonico, ContactoTelefonicoDto, String> {

    @Override
    public ContactoTelefonicoDto toDto(ContactoTelefonico entity) {
        if (entity == null) {
            return null;
        }
        ContactoTelefonicoDto dto = new ContactoTelefonicoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setTipoContacto(entity.getTipoContacto());
        dto.setObservacion(entity.getObservacion());
        dto.setTelefono(entity.getTelefono());
        dto.setTipoTelefono(entity.getTipoTelefono());
        return dto;
    }

    @Override
    public ContactoTelefonico toEntity(ContactoTelefonicoDto dto) {
        if (dto == null) {
            return null;
        }
        ContactoTelefonico entity = new ContactoTelefonico();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(ContactoTelefonicoDto dto, ContactoTelefonico entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setTipoContacto(dto.getTipoContacto());
        entity.setObservacion(dto.getObservacion());
        entity.setTelefono(dto.getTelefono());
        entity.setTipoTelefono(dto.getTipoTelefono());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
