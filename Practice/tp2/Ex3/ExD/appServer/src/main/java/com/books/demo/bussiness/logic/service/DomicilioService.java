package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.DomicilioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DomicilioService extends BaseService<Domicilio> {

    private final DomicilioRepository domicilioRepository;
    private final LocalidadService localidadService;

    public DomicilioService(DomicilioRepository domicilioRepository, LocalidadService localidadService) {
        super(domicilioRepository);
        this.domicilioRepository = domicilioRepository;
        this.localidadService = localidadService;
    }

    @Transactional
    public Domicilio crearDomicilio(Domicilio domicilio) throws ErrorServiceException {
        return alta(domicilio);
    }

    @Transactional
    public Domicilio modificarDomicilio(Long id, Domicilio datosActualizados) throws ErrorServiceException {
        return modificar(id, datosActualizados)
                .orElseThrow(() -> new ErrorServiceException("Domicilio no encontrado con id " + id));
    }

    @Transactional
    public void eliminarDomicilio(Long id) throws ErrorServiceException {
        baja(id);
    }

    @Transactional(readOnly = true)
    public List<Domicilio> listarActivos() throws ErrorServiceException {
        return super.listarActivos();
    }

    @Transactional(readOnly = true)
    public Optional<Domicilio> buscarPorId(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del domicilio no puede ser nulo.");
        }
        return obtener(id);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Domicilio entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("El domicilio no puede ser nulo.");
        }
        if (!StringUtils.hasText(entidad.getCalle())) {
            throw new ErrorServiceException("La calle es obligatoria.");
        }
        if (entidad.getNumero() == null) {
            throw new ErrorServiceException("El número es obligatorio.");
        }
        Localidad localidad = entidad.getLocalidad();
        if (localidad == null || localidad.getId() == null) {
            throw new ErrorServiceException("La localidad es obligatoria.");
        }
    }

    @Override
    protected void preAlta(Domicilio entidad) throws ErrorServiceException {
        entidad.setLocalidad(obtenerLocalidad(entidad.getLocalidad()));
        entidad.setEliminado(false);
    }

    @Override
    protected void preModificacion(Domicilio entidad) throws ErrorServiceException {
        entidad.setLocalidad(obtenerLocalidad(entidad.getLocalidad()));
    }

    private Localidad obtenerLocalidad(Localidad origen) throws ErrorServiceException {
        Long localidadId = origen != null ? origen.getId() : null;
        if (localidadId == null) {
            throw new ErrorServiceException("Debe indicar una localidad válida.");
        }
        return localidadService.buscarPorId(localidadId)
                .orElseThrow(() -> new ErrorServiceException("Localidad no encontrada con id " + localidadId));
    }
}
