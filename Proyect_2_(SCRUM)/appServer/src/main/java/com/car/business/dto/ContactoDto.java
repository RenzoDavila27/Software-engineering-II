package com.car.business.dto;

import com.car.business.domain.enums.TipoContacto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoDto extends BaseDto<String> {

    private TipoContacto tipoContacto;
    private String observacion;
}
