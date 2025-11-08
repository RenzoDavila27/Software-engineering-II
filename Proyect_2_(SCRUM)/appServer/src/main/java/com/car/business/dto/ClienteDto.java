package com.car.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClienteDto extends PersonaDto {

    private String direccionEstadia;
    private String nacionalidadId;
}
