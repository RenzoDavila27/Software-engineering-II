package com.car.business.logic.service;

import com.car.business.domain.Imagen;
import com.car.business.percistence.repository.ImagenRepository;
import org.springframework.stereotype.Service;

@Service
public class ImagenService extends BaseService<Imagen, String> {

    public ImagenService(ImagenRepository repository) {
        super(repository);
    }
}
