package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.Cliente;
import com.example.mecanic.bussines.domain.entity.Vehiculo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.mecanic.bussines.logic.service.VehiculoService;

import com.example.mecanic.bussines.logic.error.ErrorServiceException;

import com.example.mecanic.bussines.logic.service.ClienteService;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vehiculo")
public class VehiculoController extends BaseController<Vehiculo,Long> {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        super(vehiculoService);
        initController(new Vehiculo(), "LIST VEHICULO", "EDIT VEHICULO");
    }

    @Override
    protected void preAlta() throws ErrorServiceException {
        try{
            List<Cliente> clientes = clienteService.listarActivos();
            model.addAttribute("clientes", clientes);
        }catch(ErrorServiceException e){
            throw e;
        }
    }

    @Override
    protected void preModificacion() throws ErrorServiceException {
        // acciones previas a modificar si se necesitan
        try{
            List<Cliente> clientes = clienteService.listarActivos();
            model.addAttribute("clientes", clientes);
        }catch(ErrorServiceException e){
            throw e;
        }
            
    }

    @Override
    protected void preBaja() {
        // validaciones antes de eliminar
    }

}
