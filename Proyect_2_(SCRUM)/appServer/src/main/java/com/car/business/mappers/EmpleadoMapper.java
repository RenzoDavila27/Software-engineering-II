package com.car.business.mappers;

import com.car.business.domain.Empleado;
import com.car.business.domain.Persona;
import com.car.business.dto.EmpleadoDto;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper extends AbstractPersonaMapper<Empleado, EmpleadoDto> {

    public EmpleadoMapper(EntityReferenceResolver resolver) {
        super(resolver);
    }

    @Override
    protected EmpleadoDto createDto() {
        return new EmpleadoDto();
    }

    @Override
    protected Empleado createEntity() {
        return new Empleado();
    }

    @Override
    public void updateEntity(EmpleadoDto dto, Empleado entity) {
        super.updateEntity(dto, entity);
        if (dto == null || entity == null) {
            return;
        }
        entity.setTipoEmpleado(dto.getTipoEmpleado());
    }

    @Override
    protected void fillDto(Persona entity, EmpleadoDto dto) {
        super.fillDto(entity, dto);
        if (entity instanceof Empleado empleado) {
            dto.setTipoEmpleado(empleado.getTipoEmpleado());
        }
    }
}
