package com.example.demo.business.logic.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.business.domain.Estudio;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.EstudioRepository;

@Service
public class EstudioService extends BaseService<Estudio> {

    private final EstudioRepository repository;

    public EstudioService(EstudioRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public Collection<Estudio> listarActivosDesdeConsulta() throws ErrorServiceException {
        try {
            return repository.listarEstudioActivo();
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Collection<Estudio> buscarPorNombre(String nombre) throws ErrorServiceException {
        try {
            if (nombre == null || nombre.isBlank()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            Estudio estudio = repository.buscarEstudioPorNombre(nombre);
            return estudio == null ? List.of() : List.of(estudio);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Override
    protected void validar(Estudio entidad) throws ErrorServiceException {
        if (entidad.getNombre() == null || entidad.getNombre().isBlank()) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }
    }

    @Override
    protected void preCrear(Estudio entidad) throws ErrorServiceException {
        Estudio existente = repository.buscarEstudioPorNombre(entidad.getNombre());
        if (existente != null && !Boolean.TRUE.equals(existente.getEliminado())) {
            throw new ErrorServiceException("Existe un estudio con el nombre indicado");
        }
    }

    @Override
    protected void copiarPropiedades(Estudio origen, Estudio destino) throws ErrorServiceException {
        destino.setNombre(origen.getNombre());
    }
}
