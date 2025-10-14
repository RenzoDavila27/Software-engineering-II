package com.books.demo.client.dto;

import com.books.demo.bussiness.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonaDto extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellido;
    private Integer dni;
    private Long domicilioId;

    public PersonaDto() {
    }

    public PersonaDto(Long id, String nombre, String apellido, Integer dni, Long domicilioId, boolean eliminado) {
        setId(id);
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.domicilioId = domicilioId;
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

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public Long getDomicilioId() {
        return domicilioId;
    }

    public void setDomicilioId(Long domicilioId) {
        this.domicilioId = domicilioId;
    }
}
