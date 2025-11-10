package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.TipoImagen;

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