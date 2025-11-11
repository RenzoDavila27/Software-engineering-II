package com.car.clientead.client.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoTelefonicoDto extends BaseDto<String> {

    private String telefono;
    private String tipoTelefono;
}
