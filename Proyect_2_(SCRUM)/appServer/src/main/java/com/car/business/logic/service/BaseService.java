package com.car.business.logic.service;

import com.car.business.domain.BaseEntity;
import com.car.business.dto.BaseDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.BaseMapper;
import com.car.business.percistence.repository.BaseRepository;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends BaseEntity<ID>, D extends BaseDto<ID>, ID> {

    protected final BaseRepository<T, ID> repository;
    protected final BaseMapper<T, D, ID> mapper;

    protected BaseService(BaseRepository<T, ID> repository, BaseMapper<T, D, ID> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public D crear(D dto) throws BusinessException {
        T entidad = mapper.toEntity(dto);
        T guardado = alta(entidad);
        return mapper.toDto(guardado);
    }

    public Optional<D> actualizar(ID id, D dto) throws BusinessException {
        T entidadActualizada = mapper.toEntity(dto);
        return modificar(id, entidadActualizada).map(mapper::toDto);
    }

    public Optional<D> obtenerDto(ID id) throws BusinessException {
        return obtener(id).map(mapper::toDto);
    }

    public List<D> listarActivosDto() throws BusinessException {
        return listarActivos().stream()
                .map(mapper::toDto)
                .toList();
    }

    public T alta(T entidad)throws BusinessException {
        try {
            validar(entidad);
            preAlta(entidad);

            entidad.setEliminado(false);
            T guardado = repository.save(entidad);

            postAlta(guardado);
            return guardado;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BusinessException("Error de Sistemas");
        }
    }

    public Optional<T> modificar(ID id, T entidadNueva) throws BusinessException {
        try {
            validar(entidadNueva);
            preModificacion(entidadNueva);
            return repository.findById(id).map(entidad -> {
                entidadNueva.setId(id);
                T actualizado = repository.save(entidadNueva);
                return actualizado;
            });

        } catch (Exception e) {
            throw new BusinessException("Error de Sistemas");
        }
    }

    public boolean bajaLogica(ID id) throws BusinessException {
    try {
            preBaja(id);

            T entidad = repository.findById(id)
                    .orElseThrow(() -> new BusinessException("No existe la entidad con ID: " + id));

            entidad.setEliminado(true);
            T guardada = repository.save(entidad);

            postBaja(guardada);  

            return true;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error de Sistemas");
        }
    }

    public Optional<T> obtener(ID id) throws BusinessException {
        try {
            return repository.findById(id)
                    .filter(e -> !Boolean.TRUE.equals(e.getEliminado()));

        } catch (Exception e) {
            throw new BusinessException("Error de Sistemas");
        }
    }

    public List<T> listarActivos() throws BusinessException {
        try {
            return repository.findAll().stream()
                    .filter(e -> !Boolean.TRUE.equals(e.getEliminado()))
                    .toList();

        } catch (Exception e) {
            throw new BusinessException("Error de Sistemas");
        }
    }

    protected void validar(T entidad) throws BusinessException {}
    protected void preAlta(T entidad) throws BusinessException {}
    protected void postAlta(T entidad) throws BusinessException {}
    protected void preModificacion(T entidad) throws BusinessException {}
    protected void preBaja(ID id) throws BusinessException {}
    protected void postBaja(T entidad) throws BusinessException{}
}
