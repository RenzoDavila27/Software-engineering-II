package com.car.business.mappers;

import com.car.business.domain.Alquiler;
import com.car.business.domain.DetalleFactura;
import com.car.business.domain.Promocion;
import com.car.business.dto.DetalleFacturaDto;
import org.springframework.stereotype.Component;

@Component
public class DetalleFacturaMapper implements BaseMapper<DetalleFactura, DetalleFacturaDto, String> {

    private final EntityReferenceResolver resolver;

    public DetalleFacturaMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public DetalleFacturaDto toDto(DetalleFactura entity) {
        if (entity == null) {
            return null;
        }
        DetalleFacturaDto dto = new DetalleFacturaDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setCantidad(entity.getCantidad());
        dto.setSubtotal(entity.getSubtotal());
        dto.setAlquilerId(entity.getAlquiler() != null ? entity.getAlquiler().getId() : null);
        dto.setPromocionId(entity.getPromocion() != null ? entity.getPromocion().getId() : null);
        return dto;
    }

    @Override
    public DetalleFactura toEntity(DetalleFacturaDto dto) {
        if (dto == null) {
            return null;
        }
        DetalleFactura entity = new DetalleFactura();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(DetalleFacturaDto dto, DetalleFactura entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setCantidad(dto.getCantidad());
        entity.setSubtotal(dto.getSubtotal());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setAlquiler(resolver.getReference(Alquiler.class, dto.getAlquilerId()));
        entity.setPromocion(resolver.getReference(Promocion.class, dto.getPromocionId()));
    }
}
