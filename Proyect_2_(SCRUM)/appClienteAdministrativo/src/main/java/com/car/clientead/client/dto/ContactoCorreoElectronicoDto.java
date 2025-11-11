package com.car.clientead.client.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoCorreoElectronicoDto extends BaseDto<String> {

    private String email;
    private String tipoContacto;
    private String observacion;
}
