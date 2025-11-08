package com.car.business.logic.service;

import com.car.business.domain.Documentacion;
import com.car.business.percistence.repository.DocumentacionRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentacionService extends BaseService<Documentacion, String> {

    public DocumentacionService(DocumentacionRepository repository) {
        super(repository);
    }
}
