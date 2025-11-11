package com.car.clientead.client.dto;

import com.car.clientead.client.dto.enums.TipoDocumentacion;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentacionDto extends BaseDto<String> {

    private TipoDocumentacion tipoDocumentacion;
    private String observacion;
    private String pathArchivo;
    private String nombreArchivo;
}
