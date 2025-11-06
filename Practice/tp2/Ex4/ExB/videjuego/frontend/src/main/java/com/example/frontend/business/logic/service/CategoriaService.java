package com.example.frontend.business.logic.service;

import org.springframework.stereotype.Service;

import com.example.frontend.business.domain.CategoriaDto;
import com.example.frontend.business.logic.service.dto.CategoriaRequest;
import com.example.frontend.business.persistence.rest.CategoriaDAORest;
import com.example.frontend.controller.view.form.CategoriaForm;

@Service
public class CategoriaService extends BaseRestService<CategoriaDto, CategoriaForm, Long, CategoriaRequest> {

    public CategoriaService(CategoriaDAORest categoriaDAO) {
        super(categoriaDAO);
    }

    @Override
    protected CategoriaRequest toRequest(CategoriaForm form) {
        return new CategoriaRequest(form.getNombre());
    }

    @Override
    protected CategoriaForm mapToForm(CategoriaDto dto) {
        CategoriaForm form = new CategoriaForm();
        form.setId(dto.getId());
        form.setNombre(dto.getNombre());
        return form;
    }
}
