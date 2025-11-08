package com.car.business.domain;

import com.car.business.domain.enums.TipoImagen;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Imagen extends BaseEntity{

    private String nombre;
    private String mime;

    @Lob
    private byte[] contenido;

    private TipoImagen tipoImagen;

}
