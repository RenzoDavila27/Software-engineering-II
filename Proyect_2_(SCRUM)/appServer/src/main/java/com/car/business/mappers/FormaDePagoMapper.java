package com.car.business.mappers;

import com.car.business.domain.FormaDePago;
import com.car.business.dto.FormaDePagoDto;
import org.springframework.stereotype.Component;

@Component
public class FormaDePagoMapper implements BaseMapper<FormaDePago, FormaDePagoDto, String> {

    @Override
    public FormaDePagoDto toDto(FormaDePago entity) {
        if (entity == null) {
            return null;
        }
        FormaDePagoDto dto = new FormaDePagoDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setTipoPago(entity.getTipoPago());
        dto.setObservacion(entity.getObservacion());
        return dto;
    }

    @Override
    public FormaDePago toEntity(FormaDePagoDto dto) {
        if (dto == null) {
            return null;
        }
        FormaDePago entity = new FormaDePago();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(FormaDePagoDto dto, FormaDePago entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setTipoPago(dto.getTipoPago());
        entity.setObservacion(dto.getObservacion());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
