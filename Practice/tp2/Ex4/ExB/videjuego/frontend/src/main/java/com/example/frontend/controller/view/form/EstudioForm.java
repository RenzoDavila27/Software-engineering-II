package com.example.frontend.controller.view.form;

import jakarta.validation.constraints.NotBlank;

public class EstudioForm {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

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
}
