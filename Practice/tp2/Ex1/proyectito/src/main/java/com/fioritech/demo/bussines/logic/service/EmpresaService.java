package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Empresa;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa crearEmpresa(Empresa empresa) {
        verificarAtributos(empresa);
        if (empresa.getId() != null) {
            throw new BusinessException("La empresa ya tiene un id asignado");
        }
        empresa.setRazonSocial(empresa.getRazonSocial().trim());
        empresa.setEliminado(false);
        return empresaRepository.save(empresa);
    }

    public Empresa modificarEmpresa(Long id, Empresa cambios) {
        Empresa existente = obtenerEmpresaActiva(id);
        verificarAtributos(cambios);
        existente.setRazonSocial(cambios.getRazonSocial().trim());
        return empresaRepository.save(existente);
    }

    public void eliminarEmpresa(Long id) {
        Empresa existente = obtenerEmpresaActiva(id);
        existente.setEliminado(true);
        empresaRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Empresa> listarEmpresas() {
        return empresaRepository.buscarEmpresasActivas();
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresaPorId(Long id) {
        return obtenerEmpresaActiva(id);
    }

    public void verificarAtributos(Empresa empresa) {
        if (empresa == null) {
            throw new BusinessException("La empresa es obligatoria");
        }
        if (ValidationUtils.isBlank(empresa.getRazonSocial())) {
            throw new BusinessException("La razon social es obligatoria");
        }
    }

    private Empresa obtenerEmpresaActiva(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la empresa con id " + id));
        if (empresa.isEliminado()) {
            throw new BusinessException("La empresa con id " + id + " esta eliminada");
        }
        return empresa;
    }
}
