package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.LocalidadRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LocalidadService extends BaseService<Localidad> {

    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository localidadRepository) {
        super(localidadRepository);
        this.localidadRepository = localidadRepository;
    }

    @Transactional
    public Localidad crearLocalidad(Localidad localidad) throws ErrorServiceException {
        return alta(localidad);
    }

    @Transactional
    public Localidad modificarLocalidad(Long id, Localidad datosActualizados) throws ErrorServiceException {
        return modificar(id, datosActualizados)
                .orElseThrow(() -> new ErrorServiceException("Localidad no encontrada con id " + id));
    }

    @Transactional
    public void eliminarLocalidad(Long id) throws ErrorServiceException {
        baja(id);
    }

    @Transactional(readOnly = true)
    public List<Localidad> listarActivas() throws ErrorServiceException {
        return super.listarActivos();
    }

    @Transactional(readOnly = true)
    public Optional<Localidad> buscarPorId(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la localidad no puede ser nulo.");
        }
        return obtener(id);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Localidad entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("La localidad no puede ser nula.");
        }
        if (!StringUtils.hasText(entidad.getDenominacion())) {
            throw new ErrorServiceException("La denominación es obligatoria.");
        }
    }
}
