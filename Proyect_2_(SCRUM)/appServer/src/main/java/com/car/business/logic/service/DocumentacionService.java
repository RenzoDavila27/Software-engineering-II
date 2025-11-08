package com.car.business.logic.service;

import com.car.business.domain.Documentacion;
import com.car.business.dto.DocumentacionDto;
import com.car.business.mappers.DocumentacionMapper;
import com.car.business.percistence.repository.DocumentacionRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentacionService extends BaseService<Documentacion, DocumentacionDto, String> {

    public DocumentacionService(DocumentacionRepository repository, DocumentacionMapper mapper) {
        super(repository, mapper);
    }
}
