package com.car.business.dto;

import com.car.business.domain.enums.RolUsuario;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UsuarioDto extends BaseDto<String> {

    private String nombreUsuario;
    private String clave;
    private RolUsuario rolUsuario;
    private String personaId;
}
