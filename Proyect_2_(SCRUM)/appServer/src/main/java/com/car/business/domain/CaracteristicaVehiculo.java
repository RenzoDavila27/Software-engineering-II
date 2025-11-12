package com.car.business.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CaracteristicaVehiculo extends BaseEntity<String> {

    private String marca;
    private String modelo;
    private Long anio;
    private int cantidadAsientos;
    private int cantidadPuertas;
    private int cantidadTotalVehiculos = 0;
    private int cantidadTotalVehiculosAlquilados = 0;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imagen_id", referencedColumnName = "id")
    private Imagen imagen;
    


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Boolean getEliminado() { return eliminado; }
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
