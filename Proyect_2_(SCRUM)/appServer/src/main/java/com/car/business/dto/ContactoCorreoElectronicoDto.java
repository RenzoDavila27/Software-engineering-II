package com.car.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactoCorreoElectronicoDto extends ContactoDto {

    private String email;
}
