package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Persona;

public class PersonaDto {

    private Long id;
    private String nombre;
    private String apellido;
    private Integer dni;
    private Long domicilioId;
    private boolean eliminado;

    public PersonaDto() {
    }

    public PersonaDto(Long id, String nombre, String apellido, Integer dni, Long domicilioId, boolean eliminado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.domicilioId = domicilioId;
        this.eliminado = eliminado;
    }

    public static PersonaDto fromEntity(Persona persona) {
        if (persona == null) {
            return null;
        }
        Domicilio domicilio = persona.getDomicilio();
        Long domicilioId = domicilio != null ? domicilio.getId() : null;
        return new PersonaDto(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getDni(),
                domicilioId,
                persona.isEliminado()
        );
    }

    public Persona toEntity() {
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setDni(dni);
        persona.setEliminado(eliminado);
        return persona;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}

