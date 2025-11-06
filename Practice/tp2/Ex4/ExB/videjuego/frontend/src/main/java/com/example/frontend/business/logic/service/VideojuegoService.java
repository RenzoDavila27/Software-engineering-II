package com.example.frontend.business.logic.service;

import org.springframework.stereotype.Service;

import com.example.frontend.business.domain.VideojuegoDto;
import com.example.frontend.business.logic.service.dto.VideojuegoRequest;
import com.example.frontend.business.persistence.rest.VideojuegoDAORest;
import com.example.frontend.controller.view.form.VideojuegoForm;

@Service
public class VideojuegoService extends BaseRestService<VideojuegoDto, VideojuegoForm, Long, VideojuegoRequest> {

    public VideojuegoService(VideojuegoDAORest videojuegoDAO) {
        super(videojuegoDAO);
    }

    @Override
    protected VideojuegoRequest toRequest(VideojuegoForm form) {
        return new VideojuegoRequest(
                form.getTitulo(),
                form.getRutaimg(),
                form.getPrecio(),
                form.getCantidad(),
                form.getDescripcion(),
                form.getOferta(),
                form.getFechalanzamiento(),
                form.getCategoriaId(),
                form.getEstudioId()
        );
    }

    @Override
    protected VideojuegoForm mapToForm(VideojuegoDto dto) {
        VideojuegoForm form = new VideojuegoForm();
        form.setId(dto.getId());
        form.setTitulo(dto.getTitulo());
        form.setRutaimg(dto.getRutaimg());
        form.setPrecio(dto.getPrecio());
        form.setCantidad(dto.getCantidad());
        form.setDescripcion(dto.getDescripcion());
        form.setOferta(dto.getOferta());
        form.setFechalanzamiento(dto.getFechalanzamiento());
        if (dto.getCategoria() != null) {
            form.setCategoriaId(dto.getCategoria().getId());
        }
        if (dto.getEstudio() != null) {
            form.setEstudioId(dto.getEstudio().getId());
        }
        return form;
    }
}
