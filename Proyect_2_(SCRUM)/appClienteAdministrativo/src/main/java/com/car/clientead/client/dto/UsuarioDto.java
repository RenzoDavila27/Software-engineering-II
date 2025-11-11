package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.RolUsuario;

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
