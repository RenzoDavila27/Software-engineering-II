package com.example.frontend.business.logic.service;

import org.springframework.stereotype.Service;

import com.example.frontend.business.domain.EstudioDto;
import com.example.frontend.business.logic.service.dto.EstudioRequest;
import com.example.frontend.business.persistence.rest.EstudioDAORest;
import com.example.frontend.controller.view.form.EstudioForm;

@Service
public class EstudioService extends BaseRestService<EstudioDto, EstudioForm, Long, EstudioRequest> {

    public EstudioService(EstudioDAORest estudioDAO) {
        super(estudioDAO);
    }

    @Override
    protected EstudioRequest toRequest(EstudioForm form) {
        return new EstudioRequest(form.getNombre());
    }

    @Override
    protected EstudioForm mapToForm(EstudioDto dto) {
        EstudioForm form = new EstudioForm();
        form.setId(dto.getId());
        form.setNombre(dto.getNombre());
        return form;
    }
}
