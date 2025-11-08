package com.car.business.dto;

import com.car.business.domain.enums.TipoTelefono;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoTelefonicoDto extends ContactoDto {

    private String telefono;
    private TipoTelefono tipoTelefono;
}
