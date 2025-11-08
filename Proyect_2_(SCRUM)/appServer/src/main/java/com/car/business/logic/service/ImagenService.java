package com.car.business.logic.service;

import com.car.business.domain.Imagen;
import com.car.business.dto.ImagenDto;
import com.car.business.mappers.ImagenMapper;
import com.car.business.percistence.repository.ImagenRepository;
import org.springframework.stereotype.Service;

@Service
public class ImagenService extends BaseService<Imagen, ImagenDto, String> {

    public ImagenService(ImagenRepository repository, ImagenMapper mapper) {
        super(repository, mapper);
    }
}
