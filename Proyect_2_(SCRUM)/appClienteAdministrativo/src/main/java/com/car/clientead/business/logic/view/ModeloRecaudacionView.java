package com.car.clientead.business.logic.view;

import lombok.Data;

@Data
public class ModeloRecaudacionView {

    private String caracteristicaId;
    private String marca;
    private String modelo;
    private Long anio;
    private long cantidadAlquileres;
    private double totalRecaudado;

    public String getEtiquetaModelo() {
        StringBuilder sb = new StringBuilder();
        if (marca != null) {
            sb.append(marca.trim());
        }
        if (modelo != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(modelo.trim());
        }
        if (anio != null && anio > 0) {
            sb.append(" (").append(anio).append(")");
        }
        return sb.length() == 0 ? "Modelo sin información" : sb.toString();
    }
}
