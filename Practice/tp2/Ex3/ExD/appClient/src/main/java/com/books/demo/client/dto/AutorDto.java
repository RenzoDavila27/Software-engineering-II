package com.books.demo.client.dto;

import com.books.demo.bussiness.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutorDto extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellido;
    private String biografia;

    public AutorDto() {
    }

    public AutorDto(Long id, String nombre, String apellido, String biografia, boolean eliminado) {
        setId(id);
        this.nombre = nombre;
        this.apellido = apellido;
        this.biografia = biografia;
        setEliminado(eliminado);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
}
