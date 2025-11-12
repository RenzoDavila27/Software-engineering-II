package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.TipoContacto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoCorreoElectronicoDto extends BaseDto<String> {

    private String email;
    private TipoContacto tipoContacto;
    private String observacion;
}
