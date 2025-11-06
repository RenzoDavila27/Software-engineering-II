package com.books.demo.bussiness.domain;

import com.books.demo.bussiness.domain.prototype.Prototype;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "autor")
public class Autor extends BaseEntity implements Prototype<Autor> {

    private String nombre;

    private String apellido;

    private String biografia;

    public Autor() {
    }

    public Autor(String nombre, String apellido, String biografia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.biografia = biografia;
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

    @Override
    public Autor clonar() {
        Autor copia = new Autor();
        copia.setNombre(this.nombre);
        copia.setApellido(this.apellido);
        copia.setBiografia(this.biografia);
        copia.setEliminado(false);
        return copia;
    }
}
