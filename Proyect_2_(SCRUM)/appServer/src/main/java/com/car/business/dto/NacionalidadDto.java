package com.car.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NacionalidadDto extends BaseDto<String> {

    private String nombre;
}
