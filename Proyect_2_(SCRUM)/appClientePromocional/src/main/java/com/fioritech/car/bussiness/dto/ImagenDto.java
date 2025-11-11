package com.fioritech.car.bussiness.dto;

import com.fioritech.car.bussiness.domain.enums.TipoImagen;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Base64;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImagenDto extends BaseDto<String> {

    private String nombre;
    private String mime;
    private byte[] contenido;
    private TipoImagen tipoImagen;

    public String getContenidoAsBase64() {
        if (contenido == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(contenido);
    }
}
