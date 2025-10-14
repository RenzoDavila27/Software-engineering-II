package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.Autor;

public class AutorDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String biografia;
    private boolean eliminado;

    public AutorDto() {
    }

    public AutorDto(Long id, String nombre, String apellido, String biografia, boolean eliminado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.biografia = biografia;
        this.eliminado = eliminado;
    }

    public static AutorDto fromEntity(Autor autor) {
        if (autor == null) {
            return null;
        }
        return new AutorDto(
                autor.getId(),
                autor.getNombre(),
                autor.getApellido(),
                autor.getBiografia(),
                autor.isEliminado()
        );
    }

    public Autor toEntity() {
        Autor autor = new Autor();
        autor.setNombre(nombre);
        autor.setApellido(apellido);
        autor.setBiografia(biografia);
        autor.setEliminado(eliminado);
        return autor;
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

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}

