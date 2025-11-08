package com.car.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmpresaDto extends BaseDto<String> {

    private String nombre;
    private String personaId;
    private String contactoId;
}
