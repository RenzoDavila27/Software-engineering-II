package com.contactos.controller.view.dto;

import java.util.ArrayList;
import java.util.List;

public class EmpresaForm {

    private Long id;
    private String nombre;
    private List<ContactoCorreoForm> correos = new ArrayList<>();
    private List<ContactoTelefonoForm> telefonos = new ArrayList<>();
    private List<Long> contactosEliminar = new ArrayList<>();

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

    public List<ContactoCorreoForm> getCorreos() {
        return correos;
    }

    public void setCorreos(List<ContactoCorreoForm> correos) {
        this.correos = correos;
    }

    public List<ContactoTelefonoForm> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<ContactoTelefonoForm> telefonos) {
        this.telefonos = telefonos;
    }

    public List<Long> getContactosEliminar() {
        return contactosEliminar;
    }

    public void setContactosEliminar(List<Long> contactosEliminar) {
        this.contactosEliminar = contactosEliminar;
    }
}
