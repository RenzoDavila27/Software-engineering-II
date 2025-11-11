package com.car.business.logic.service;

import com.car.business.domain.Promocion;
import com.car.business.dto.PromocionDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.PromocionMapper;
import com.car.business.percistence.repository.PromocionRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PromocionService extends BaseService<Promocion, PromocionDto, String> {

    public PromocionService(PromocionRepository repository, PromocionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Promocion entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La promoción es obligatoria.");
        }
        if (entidad.getPorcentajeDescuento() <= 0 || entidad.getPorcentajeDescuento() > 100) {
            throw new BusinessException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        if (entidad.getCodigoDescuento() <= 0) {
            throw new BusinessException("El código de descuento debe ser mayor a cero.");
        }
        if (!StringUtils.hasText(entidad.getDescripcionDescuento())) {
            throw new BusinessException("La descripción de la promoción es obligatoria.");
        }
        LocalDate desde = entidad.getFechaDesde();
        LocalDate hasta = entidad.getFechaHasta();
        if (desde == null || hasta == null) {
            throw new BusinessException("Las fechas de vigencia son obligatorias.");
        }
        if (hasta.isBefore(desde)) {
            throw new BusinessException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
    }
}
