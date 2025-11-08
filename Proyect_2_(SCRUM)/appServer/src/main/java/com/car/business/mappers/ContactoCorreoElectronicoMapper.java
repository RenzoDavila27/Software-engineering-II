package com.car.business.mappers;

import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.dto.ContactoCorreoElectronicoDto;
import org.springframework.stereotype.Component;

@Component
public class ContactoCorreoElectronicoMapper implements BaseMapper<ContactoCorreoElectronico, ContactoCorreoElectronicoDto, String> {

    @Override
    public ContactoCorreoElectronicoDto toDto(ContactoCorreoElectronico entity) {
        if (entity == null) {
            return null;
        }
        ContactoCorreoElectronicoDto dto = new ContactoCorreoElectronicoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setTipoContacto(entity.getTipoContacto());
        dto.setObservacion(entity.getObservacion());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    @Override
    public ContactoCorreoElectronico toEntity(ContactoCorreoElectronicoDto dto) {
        if (dto == null) {
            return null;
        }
        ContactoCorreoElectronico entity = new ContactoCorreoElectronico();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(ContactoCorreoElectronicoDto dto, ContactoCorreoElectronico entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setTipoContacto(dto.getTipoContacto());
        entity.setObservacion(dto.getObservacion());
        entity.setEmail(dto.getEmail());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
