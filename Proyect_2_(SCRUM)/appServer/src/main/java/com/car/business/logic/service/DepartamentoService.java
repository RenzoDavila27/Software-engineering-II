package com.car.business.logic.service;

import com.car.business.domain.Departamento;
import com.car.business.domain.Provincia;
import com.car.business.dto.DepartamentoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.DepartamentoMapper;
import com.car.business.percistence.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class DepartamentoService extends BaseService<Departamento, DepartamentoDto, String> {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository repository, DepartamentoMapper mapper) {
        super(repository, mapper);
        this.departamentoRepository = repository;
    }

    @Override
    protected void validar(Departamento entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El departamento es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre del departamento es obligatorio.");
        }
        if (entidad.getProvincia() == null) {
            throw new BusinessException("La provincia asociada es obligatoria.");
        }
    }

    public Optional<Departamento> findByNombreAndProvincia(String nombre, Provincia provincia) {

        Optional<Departamento> value = departamentoRepository.findByNombreAndProvincia(nombre, provincia);
        return value;

    }
}
