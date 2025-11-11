package com.car.clientead.business.logic.view;

import java.util.Collections;
import java.util.List;

import com.car.clientead.client.dto.ClienteDto;

import lombok.Data;

@Data
public class ClienteResumenView {

    private ClienteDto cliente;
    private String nacionalidadNombre;
    private List<ClienteAlquilerInfo> alquileres = Collections.emptyList();
    private double totalPagado;
    private String telefonoPrincipal;
    private String whatsappUrl;
    private String contactoResumen;
    private String direccionResumen;
}
