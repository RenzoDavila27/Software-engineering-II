package com.car.business.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
    private int cantidadTotalVehiculos;
    private int cantidadTotalVehiculosAlquilados;

    @ManyToOne(optional = false)
    private CostoVehiculo costoVehiculo;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Boolean getEliminado() { return eliminado; }
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
