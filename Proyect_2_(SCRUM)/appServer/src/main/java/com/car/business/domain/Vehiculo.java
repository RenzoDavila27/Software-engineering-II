package com.car.business.domain;

import com.car.business.domain.enums.EstadoVehiculo;
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
public class Vehiculo extends BaseEntity<String> {

    private EstadoVehiculo estadoVehiculo;
    private String patente;

    @ManyToOne(optional = false)
    private CaracteristicaVehiculo caracteristicaVehiculo;

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    @Override
    public Boolean getEliminado() { return eliminado; }

    @Override
    public void setEliminado(Boolean eliminado) { this.eliminado = eliminado; }
}
