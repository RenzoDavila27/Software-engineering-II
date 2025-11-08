package com.car.business.domain;

import com.car.business.domain.enums.TipoDocumentacion;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Documentacion extends BaseEntity{

    private TipoDocumentacion tipoDocumentacion;
    private String observacion;
    private String pathArchivo;
    private String nombreArchivo;

}
