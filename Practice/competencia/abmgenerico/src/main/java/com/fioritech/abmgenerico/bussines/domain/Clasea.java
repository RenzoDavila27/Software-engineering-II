package com.example.abmgenerico.business.domain;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="Clasea")
public class Clasea implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="str1")
    private String str1;

    @Column(name = "RutaImg")
    private String rutaimg;

    @Column(name="float1")
    private float float1;

    @Column(name="Short1")
    private Short short1;

    @Column(name="Str2")
    private String str2;

    @Column(name="Bool1")
    private Boolean bool1;

    @Column(name="str3")
    private String str3;

    @Column(name="Activo")
    private Boolean activo = true;

    //RELACIONES//
    /*@ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="fk_estudio")
    private Estudio estudio;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="fk_categoria")
    private Categoria categoria;
*/

    //CONSTRUCTORES//

    public Clasea(){

    }
    public Clasea(String str1, String rutaimg, float float1, Short short1, String str2, Boolean bool1,
            String str3, Boolean activo) {
        this.str1 = str1;
        this.rutaimg = rutaimg;
        this.float1 = float1;
        this.short1 = short1;
        this.str2 = str2;
        this.bool1 = bool1;
        this.str3 = str3;
        this.activo = activo;
    }

    //GETTERS Y SETTERS//

    public Long getId() {
        return this.id;
    }

    public String getStr1() {
        return this.str1;
    }

    public String getRutaimg() {
        return this.rutaimg;
    }

    public float getFloat1() {
        return this.float1;
    }

    public Short getShort1() {
        return this.short1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public void setRutaimg(String rutaimg) {
        this.rutaimg = rutaimg;
    }

    public void setFloat1(float float1) {
        this.float1 = float1;
    }

    public void setShort1(Short short1) {
        this.short1 = short1;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public void setBool1(Boolean bool1) {
        this.bool1 = bool1;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getStr2() {
        return this.str2;
    }

    public Boolean getBool1() {
        return this.bool1;
    }

    public String getStr3() {
        return this.str3;
    }

    public Boolean getActivo() {return this.activo;}
}
