package com.car.business.domain;

import com.car.business.domain.enums.TipoImagen;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@Entity
public class Imagen extends BaseEntity<String> {

    private String nombre;
    private String mime;

    @Lob
    private byte[] contenido;

    private TipoImagen tipoImagen;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
