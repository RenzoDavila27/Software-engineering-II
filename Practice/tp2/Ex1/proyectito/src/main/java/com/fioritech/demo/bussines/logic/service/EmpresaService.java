package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Empresa;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpresaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Empresa crearEmpresa(Empresa empresa) {
        verificarAtributos(empresa);
        if (empresa.getId() != null) {
            throw new BusinessException("La empresa ya tiene un id asignado");
        }
        empresa.setRazonSocial(empresa.getRazonSocial().trim());
        empresa.setEliminado(false);
        entityManager.persist(empresa);
        return empresa;
    }

    public Empresa modificarEmpresa(Long id, Empresa cambios) {
        Empresa existente = entityManager.find(Empresa.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la empresa con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La empresa con id " + id + " esta eliminada");
        }
        verificarAtributos(cambios);
        existente.setRazonSocial(cambios.getRazonSocial().trim());
        return entityManager.merge(existente);
    }

    public void eliminarEmpresa(Long id) {
        Empresa existente = entityManager.find(Empresa.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la empresa con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La empresa con id " + id + " ya esta eliminada");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Empresa empresa) {
        if (empresa == null) {
            throw new BusinessException("La empresa es obligatoria");
        }
        if (ValidationUtils.isBlank(empresa.getRazonSocial())) {
            throw new BusinessException("La razon social es obligatoria");
        }
    }
}
