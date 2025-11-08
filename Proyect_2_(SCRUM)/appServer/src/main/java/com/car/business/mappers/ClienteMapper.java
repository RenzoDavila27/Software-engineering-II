package com.car.business.mappers;

import com.car.business.domain.Cliente;
import com.car.business.domain.Nacionalidad;
import com.car.business.domain.Persona;
import com.car.business.dto.ClienteDto;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper extends AbstractPersonaMapper<Cliente, ClienteDto> {

    public ClienteMapper(EntityReferenceResolver resolver) {
        super(resolver);
    }

    @Override
    protected ClienteDto createDto() {
        return new ClienteDto();
    }

    @Override
    protected Cliente createEntity() {
        return new Cliente();
    }

    @Override
    public void updateEntity(ClienteDto dto, Cliente entity) {
        super.updateEntity(dto, entity);
        if (dto == null || entity == null) {
            return;
        }
        entity.setDireccionEstadia(dto.getDireccionEstadia());
        entity.setNacionalidad(resolver.getReference(Nacionalidad.class, dto.getNacionalidadId()));
    }

    @Override
    protected void fillDto(Persona entity, ClienteDto dto) {
        super.fillDto(entity, dto);
        if (entity instanceof Cliente cliente) {
            dto.setDireccionEstadia(cliente.getDireccionEstadia());
            dto.setNacionalidadId(cliente.getNacionalidad() != null ? cliente.getNacionalidad().getId() : null);
        }
    }
}
