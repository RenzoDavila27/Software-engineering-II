package com.car.business.dto;

import com.car.business.domain.enums.TipoEmpleado;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmpleadoDto extends PersonaDto {

    private TipoEmpleado tipoEmpleado;
}
