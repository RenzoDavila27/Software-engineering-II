package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.Cliente;
import com.example.mecanic.bussines.logic.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController extends BaseController<Cliente,Long> {

    @Autowired
    public ClienteController(ClienteService clienteService) {
        super(clienteService);
        initController(new Cliente(), "LIST CLIENTE", "EDIT CLIENTE");
    }

    @Override
    protected void preAlta() {
        // Por ejemplo, podrías inicializar campos por defecto
    }

    @Override
    protected void preModificacion() {
        // acciones previas a modificar si se necesitan
    }

    @Override
    protected void preBaja() {
        // validaciones antes de eliminar
    }
    
}