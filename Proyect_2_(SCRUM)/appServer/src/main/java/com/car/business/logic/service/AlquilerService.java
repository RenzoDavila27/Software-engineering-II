package com.car.business.logic.service;

import com.car.business.domain.Alquiler;
import com.car.business.dto.AlquilerDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.AlquilerMapper;
import com.car.business.percistence.repository.AlquilerRepository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class AlquilerService extends BaseService<Alquiler, AlquilerDto, String> {

    @Autowired 
    private AlquilerRepository alquilerRepository;

    public AlquilerService(AlquilerRepository repository, AlquilerMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Alquiler entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El alquiler es obligatorio.");
        }
        if (entidad.getCliente() == null) {
            throw new BusinessException("El cliente del alquiler es obligatorio.");
        }
        LocalDate fechaDesde = entidad.getFechaDesde();
        if (fechaDesde == null) {
            throw new BusinessException("La fecha desde es obligatoria.");
        }
        LocalDate fechaHasta = entidad.getFechaHasta();
        if (fechaHasta == null) {
            throw new BusinessException("La fecha hasta es obligatoria.");
        }
        if (entidad.getDocumentacion() == null) {
            throw new BusinessException("La documentación asociada es obligatoria.");
        }
        if (entidad.getVehiculo() == null) {
            throw new BusinessException("El vehículo es obligatorio.");
        }
        if (fechaHasta.isBefore(fechaDesde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
    }


    public List<Alquiler> buscarAlquileresVecManiana(LocalDate maniana) throws BusinessException{
        return alquilerRepository.buscarAlquilerVecManiana(maniana);
    }

}
