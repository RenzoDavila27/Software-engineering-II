package com.car.business.mappers;

import com.car.business.domain.DetalleFactura;
import com.car.business.domain.Factura;
import com.car.business.dto.FacturaDto;
import org.springframework.stereotype.Component;

@Component
public class FacturaMapper implements BaseMapper<Factura, FacturaDto, String> {

    private final EntityReferenceResolver resolver;

    public FacturaMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public FacturaDto toDto(Factura entity) {
        if (entity == null) {
            return null;
        }
        FacturaDto dto = new FacturaDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setNumeroFactura(entity.getNumeroFactura());
        dto.setFechaFactura(entity.getFechaFactura());
        dto.setTotalPagado(entity.getTotalPagado());
        dto.setEstado(entity.getEstado());
        dto.setDetalleIds(MapperUtils.toIds(entity.getDetalles()));
        return dto;
    }

    @Override
    public Factura toEntity(FacturaDto dto) {
        if (dto == null) {
            return null;
        }
        Factura entity = new Factura();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(FacturaDto dto, Factura entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNumeroFactura(dto.getNumeroFactura());
        entity.setFechaFactura(dto.getFechaFactura());
        entity.setTotalPagado(dto.getTotalPagado());
        entity.setEstado(dto.getEstado());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        if (dto.getDetalleIds() != null) {
            entity.setDetalles(resolver.getReferences(DetalleFactura.class, dto.getDetalleIds()));
        }
    }
}
