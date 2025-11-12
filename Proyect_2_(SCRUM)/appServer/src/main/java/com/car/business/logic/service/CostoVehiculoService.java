package com.car.business.logic.service;

import com.car.business.domain.CostoVehiculo;
import com.car.business.domain.Vehiculo;
import com.car.business.dto.CostoVehiculoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.CostoVehiculoMapper;
import com.car.business.percistence.repository.CostoVehiculoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CostoVehiculoService extends BaseService<CostoVehiculo, CostoVehiculoDto, String> {

    @Autowired
    private CostoVehiculoRepository costoVehiculoRepository;

    public CostoVehiculoService(CostoVehiculoRepository repository, CostoVehiculoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(CostoVehiculo entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El costo del vehículo es obligatorio.");
        }
        LocalDate desde = entidad.getFechaDesde();
        if (desde == null) {
            throw new BusinessException("La fecha desde es obligatoria.");
        }
        LocalDate hasta = entidad.getFechaHasta();
        if (hasta.isBefore(desde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
        if (entidad.getCosto() == null || entidad.getCosto() <= 0) {
            throw new BusinessException("El costo debe ser mayor a cero.");
        }
    }

    @Override    
    protected void preAlta(CostoVehiculo costoVehiculo) throws BusinessException{
        CostoVehiculo oldCostoVehiculo = costoVehiculoRepository.buscarCostoVehiculoActual(costoVehiculo.getCaracteristicaVehiculo().getId(),LocalDate.of(9999,1,1));
        if (oldCostoVehiculo!=null){

            if (costoVehiculo.getFechaHasta()!=LocalDate.of(9999,1,1)) {
                oldCostoVehiculo.setFechaHasta(costoVehiculo.getFechaHasta());
            }else{
                oldCostoVehiculo.setFechaHasta(LocalDate.now());
            }
            repository.save(oldCostoVehiculo);
        }

    }

    public List<CostoVehiculo> listarCostosPorVehiculo(String id) throws BusinessException{
        return costoVehiculoRepository.listarCostosPorVehiculo(id);

    }

    public CostoVehiculo obtenerCostoVigente(String caracteristicaVehiculoId) {
        if (!StringUtils.hasText(caracteristicaVehiculoId)) {
            throw new BusinessException("El identificador de la característica es obligatorio.");
        }
        CostoVehiculo costo = costoVehiculoRepository.buscarCostoVehiculoActual(
            caracteristicaVehiculoId, LocalDate.of(9999, 1, 1));
        if (costo == null || Boolean.TRUE.equals(costo.getEliminado())) {
            throw new BusinessException("No se encontró un costo vigente para el vehículo seleccionado.");
        }
        return costo;
    }


}
