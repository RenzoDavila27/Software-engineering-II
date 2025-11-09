package com.car.business.logic.service;

import com.car.business.domain.Imagen;
import com.car.business.dto.ImagenDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.ImagenMapper;
import com.car.business.percistence.repository.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ImagenService extends BaseService<Imagen, ImagenDto, String> {

    public ImagenService(ImagenRepository repository, ImagenMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Imagen entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La imagen es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre de la imagen es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getMime())) {
            throw new BusinessException("El tipo MIME es obligatorio.");
        }
        if (entidad.getContenido() == null) {
            throw new BusinessException("El contenido de la imagen es obligatorio.");
        }
        if (entidad.getTipoImagen() == null) {
            throw new BusinessException("El tipo de imagen es obligatorio.");
        }
    }
}
