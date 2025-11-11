package com.car.business.mappers;

import com.car.business.domain.Promocion;
import com.car.business.dto.PromocionDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PromocionMapper implements BaseMapper<Promocion, PromocionDto, String> {

    @Override
    public PromocionDto toDto(Promocion entity) {
        if (entity == null) {
            return null;
        }
        PromocionDto dto = new PromocionDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setPorcentajeDescuento(entity.getPorcentajeDescuento());
        dto.setCodigoDescuento(entity.getCodigoDescuento());
        dto.setDescripcionDescuento(entity.getDescripcionDescuento());
        dto.setFechaDesde(entity.getFechaDesde());
        dto.setFechaHasta(entity.getFechaHasta());
        return dto;
    }

    @Override
    public Promocion toEntity(PromocionDto dto) {
        if (dto == null) {
            return null;
        }
        Promocion entity = new Promocion();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(PromocionDto dto, Promocion entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        entity.setCodigoDescuento(dto.getCodigoDescuento());
        entity.setDescripcionDescuento(StringUtils.hasText(dto.getDescripcionDescuento()) ? dto.getDescripcionDescuento() : null);
        entity.setFechaDesde(dto.getFechaDesde());
        entity.setFechaHasta(dto.getFechaHasta());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
    }
}
