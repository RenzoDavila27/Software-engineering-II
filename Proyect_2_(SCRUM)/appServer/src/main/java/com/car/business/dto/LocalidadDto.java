package com.car.business.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocalidadDto extends BaseDto<String> {

    private String nombre;
    private String codigoPostal;
    private String departamentoId;
    private List<String> direccionIds;
}
