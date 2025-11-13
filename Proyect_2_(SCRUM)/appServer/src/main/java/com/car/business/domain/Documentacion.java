package com.car.business.domain;

import com.car.business.domain.enums.TipoDocumentacion;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Documentacion extends BaseEntity<String> {

    private TipoDocumentacion tipoDocumentacion;
    private String observacion;
    private String pathArchivo = "/home/renzo";
    private String nombreArchivo;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
