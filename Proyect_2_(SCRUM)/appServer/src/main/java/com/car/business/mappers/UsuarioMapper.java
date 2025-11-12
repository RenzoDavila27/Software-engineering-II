package com.car.business.mappers;

import com.car.business.domain.*;
import com.car.business.domain.enums.TipoContacto;
import com.car.business.domain.enums.TipoDocumento;
import com.car.business.domain.enums.TipoTelefono;
import com.car.business.dto.ContactApiDto;
import com.car.business.dto.UsuarioDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<Contacto> mapContactos(List<ContactApiDto> contacts, String emailFromDto) {
        List<Contacto> resultContacts = new ArrayList<>();

        if (contacts != null && !contacts.isEmpty()) {
            for (ContactApiDto contactApiDto : contacts) {
                TipoContacto tipoContacto;
                try {
                    tipoContacto = TipoContacto.valueOf(contactApiDto.getType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Default to PERSONAL if type is unknown or not provided
                    tipoContacto = TipoContacto.PERSONAL;
                }

                if (contactApiDto.getValue().contains("(") && contactApiDto.getValue().contains(")")) {
                    // It's a phone number
                    resultContacts.add(parsePhoneNumber(contactApiDto.getValue(), tipoContacto, contactApiDto.getNote()));
                } else {
                    // It's an email
                    ContactoCorreoElectronico emailContact = new ContactoCorreoElectronico();
                    emailContact.setEmail(contactApiDto.getValue());
                    emailContact.setTipoContacto(tipoContacto);
                    emailContact.setObservacion(contactApiDto.getNote());
                    emailContact.setEliminado(false);
                    resultContacts.add(emailContact);
                }
            }
        }

        return resultContacts;
    }

    public static final Pattern PHONE_PATTERN = Pattern.compile("(.*) \\((\\w+)\\)");

    public ContactoTelefonico parsePhoneNumber(String value, TipoContacto tipoContacto, String note) {
        Matcher matcher = PHONE_PATTERN.matcher(value);
        if (matcher.matches()) {
            String phoneNumber = matcher.group(1);
            String tipoTelefonoStr = matcher.group(2);
            TipoTelefono tipoTelefono = null;
            try {
                tipoTelefono = TipoTelefono.valueOf(tipoTelefonoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Default to CELULAR if type is unknown
                tipoTelefono = TipoTelefono.CELULAR;
            }

            ContactoTelefonico phoneContact = new ContactoTelefonico();
            phoneContact.setTelefono(phoneNumber);
            phoneContact.setTipoTelefono(tipoTelefono);
            phoneContact.setTipoContacto(tipoContacto);
            phoneContact.setObservacion(note);
            phoneContact.setEliminado(false);
            return phoneContact;
        }
        return null; // Invalid phone format
    }

    public TipoDocumento mapTipoDocumento(String documentType) {

        return TipoDocumento.valueOf(documentType.toUpperCase());

    }

}
