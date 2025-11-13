package com.car.clientead.client.dto.form;

import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.dto.enums.TipoTelefono;
import lombok.Data;

@Data
public class ClienteContactoForm {
    private TipoContacto tipoContacto;
    private MetodoContacto metodoContacto; // EMAIL o PHONE
    private String valor;
    private TipoTelefono tipoTelefono;
    private String observacion;

    public enum MetodoContacto {
        EMAIL,
        PHONE
    }
}
