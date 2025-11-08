package com.car.business.dto;

import com.car.business.domain.enums.TipoDocumentacion;
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
