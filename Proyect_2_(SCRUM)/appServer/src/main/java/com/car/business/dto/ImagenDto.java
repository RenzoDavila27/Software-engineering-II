package com.car.business.dto;

import com.car.business.domain.enums.TipoImagen;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImagenDto extends BaseDto<String> {

    private String nombre;
    private String mime;
    private byte[] contenido;
    private TipoImagen tipoImagen;
}
