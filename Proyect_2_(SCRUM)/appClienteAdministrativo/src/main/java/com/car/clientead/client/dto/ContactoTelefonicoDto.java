package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.dto.enums.TipoTelefono;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoTelefonicoDto extends BaseDto<String> {

    private String telefono;
    private TipoTelefono tipoTelefono;
    private TipoContacto tipoContacto;
    private String observacion;
}
