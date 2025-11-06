package com.example.frontend.business.logic.service;

import java.util.List;

import com.example.frontend.business.logic.error.ErrorServiceException;
import com.example.frontend.business.persistence.rest.BaseRestDAO;

public abstract class BaseRestService<DTO, FORM, ID, REQUEST> {

    private final BaseRestDAO<DTO, ID, REQUEST> dao;

    protected BaseRestService(BaseRestDAO<DTO, ID, REQUEST> dao) {
        this.dao = dao;
    }

    public List<DTO> listar() throws ErrorServiceException {
        return dao.listar();
    }

    public DTO obtener(ID id) throws ErrorServiceException {
        return dao.buscar(id);
    }

    public void crear(FORM form) throws ErrorServiceException {
        dao.crear(toRequest(form));
    }

    public void actualizar(ID id, FORM form) throws ErrorServiceException {
        dao.actualizar(id, toRequest(form));
    }

    public void eliminar(ID id) throws ErrorServiceException {
        dao.eliminar(id);
    }

    public FORM toForm(DTO dto) {
        return mapToForm(dto);
    }

    protected abstract REQUEST toRequest(FORM form);

    protected abstract FORM mapToForm(DTO dto);
}
