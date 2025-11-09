package com.car.business.logic.service;

import com.car.business.domain.Documentacion;
import com.car.business.dto.DocumentacionDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.DocumentacionMapper;
import com.car.business.percistence.repository.DocumentacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DocumentacionService extends BaseService<Documentacion, DocumentacionDto, String> {

    public DocumentacionService(DocumentacionRepository repository, DocumentacionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Documentacion entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La documentación es obligatoria.");
        }
        if (entidad.getTipoDocumentacion() == null) {
            throw new BusinessException("El tipo de documentación es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNombreArchivo())) {
            throw new BusinessException("El nombre del archivo es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getPathArchivo())) {
            throw new BusinessException("La ruta del archivo es obligatoria.");
        }
    }
}
