package com.car.business.mappers;

import com.car.business.domain.Alquiler;
import com.car.business.domain.Cliente;
import com.car.business.domain.Documentacion;
import com.car.business.domain.Vehiculo;
import com.car.business.dto.AlquilerDto;
import org.springframework.stereotype.Component;

@Component
public class AlquilerMapper implements BaseMapper<Alquiler, AlquilerDto, String> {

    private final EntityReferenceResolver resolver;

    public AlquilerMapper(EntityReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public AlquilerDto toDto(Alquiler entity) {
        if (entity == null) {
            return null;
        }
        AlquilerDto dto = new AlquilerDto();
        dto.setId(entity.getId());
        dto.setEliminado(entity.getEliminado());
        dto.setClienteId(entity.getCliente() != null ? entity.getCliente().getId() : null);
        dto.setFechaDesde(entity.getFechaDesde());
        dto.setFechaHasta(entity.getFechaHasta());
        dto.setDocumentacionId(entity.getDocumentacion() != null ? entity.getDocumentacion().getId() : null);
        dto.setVehiculoId(entity.getVehiculo() != null ? entity.getVehiculo().getId() : null);
        dto.setCaracteristicaVehiculoId(entity.getVehiculo() != null
                && entity.getVehiculo().getCaracteristicaVehiculo() != null
                ? entity.getVehiculo().getCaracteristicaVehiculo().getId()
                : null);
        return dto;
    }

    @Override
    public Alquiler toEntity(AlquilerDto dto) {
        if (dto == null) {
            return null;
        }
        Alquiler entity = new Alquiler();
        entity.setId(dto.getId());
        updateEntity(dto, entity);
        return entity;
    }

    @Override
    public void updateEntity(AlquilerDto dto, Alquiler entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setFechaDesde(dto.getFechaDesde());
        entity.setFechaHasta(dto.getFechaHasta());
        entity.setEliminado(Boolean.TRUE.equals(dto.getEliminado()));
        entity.setCliente(resolver.getReference(Cliente.class, dto.getClienteId()));
        entity.setDocumentacion(resolver.getReference(Documentacion.class, dto.getDocumentacionId()));
        entity.setVehiculo(resolver.getReference(Vehiculo.class, dto.getVehiculoId()));
    }
}
