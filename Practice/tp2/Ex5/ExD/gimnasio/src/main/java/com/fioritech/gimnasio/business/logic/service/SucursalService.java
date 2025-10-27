package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Empresa;
import com.fioritech.gimnasio.business.domain.Sucursal;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.SucursalRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SucursalService {

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private DireccionService direccionService;



    public Sucursal crearSucursal(String nombre, String idEmpresa, Direccion direccion) throws BusinessException{
        try{
            direccionService.crearDireccion(direccion.getCalle(),direccion.getNumeracion(),direccion.getBarrio(),direccion.getManzanaPiso(),direccion.getCasaDepartamento(),direccion.getReferencia(),direccion.getLocalidad().getId());
            Empresa empresa = empresaService.buscarEmpresa(idEmpresa);
            validar(nombre, direccion);
            Sucursal sucursal = new Sucursal();
            sucursal.setNombre(nombre.trim());
            sucursal.setEmpresa(empresa);
            sucursal.setDireccion(direccion);
            return sucursalRepository.save(sucursal);
        }catch(BusinessException e){
            throw e;
        }
       
    }

    public void validar(String nombre, Direccion direccion) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de la sucursal es obligatorio");
        }
        if (direccion == null) {
            throw new BusinessException("La direccion de la sucursal es obligatoria");
        }
        Sucursal existente = sucursalRepository.buscarSucursalPorNombre(nombre.trim());
        if (existente!=null) {
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        }
    }

    @Transactional(readOnly = true)
    public Sucursal buscarSucursal(String id) {
        try{
            Sucursal sucursal = sucursalRepository.buscarSucursalPorId(id);    
            if (sucursal == null) {
                throw new BusinessException("Sucursal no encontrada");
            } 
            return sucursal;
        }catch(BusinessException e){
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Sucursal buscarSucursalPorNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return sucursalRepository.findAll().stream()
            .filter(s -> !s.isEliminado())
            .filter(s -> s.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
    }

    @Transactional(readOnly = true)
    public Sucursal buscarSucursalPorNombre(String nombre, Empresa empresa) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return sucursalRepository.findAll().stream()
            .filter(s -> !s.isEliminado())
            .filter(s -> s.getEmpresa().getId().equals(empresa.getId()))
            .filter(s -> s.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
    }

    public Sucursal modificarSucursal(String id, String nombre, String idEmpresa, Direccion direccion) {
        Sucursal sucursal = buscarSucursal(id);
        Empresa empresa = sucursal.getEmpresa();
        if (idEmpresa != null && !idEmpresa.isBlank() && (empresa == null || !empresa.getId().equals(idEmpresa))) {
            empresa = empresaService.buscarEmpresa(idEmpresa);
        }
        if (nombre != null && !nombre.isBlank()) {
            sucursal.setNombre(nombre.trim());
        }
        if (direccion != null) {
            sucursal.setDireccion(direccion);
        }
        sucursal.setEmpresa(empresa);
        return sucursalRepository.save(sucursal);
    }

    public void eliminarSucursal(String id) {
        Sucursal sucursal = buscarSucursal(id);
        sucursal.setEliminado(true);
        sucursalRepository.save(sucursal);
    }

    @Transactional(readOnly = true)
    public List<Sucursal> listarSucursal() {
        return sucursalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Sucursal> listarSucursalActiva() {
        return sucursalRepository.findAll().stream()
            .filter(s -> !s.isEliminado())
            .toList();
    }
}
