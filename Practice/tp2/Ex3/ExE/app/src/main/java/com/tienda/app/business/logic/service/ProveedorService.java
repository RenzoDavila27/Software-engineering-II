package com.tienda.app.business.logic.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.tienda.app.business.logic.service.BaseUseCaseService;
import com.tienda.app.business.logic.service.GeocodingService;
import com.tienda.app.business.domain.Proveedor;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.ProveedorRepository;

import com.tienda.app.business.logic.service.BaseService;

@Service
public class ProveedorService extends BaseService<Proveedor, Long> {

    @Autowired
    private GeocodingService geocodingService;

    public ProveedorService(ProveedorRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Proveedor proveedor) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (proveedor == null) {
                    throw new ErrorServiceException("Debe indicar el proveedor");
                }

                if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre del proveedor");
                }

                if (proveedor.isEliminado()) {
                    throw new ErrorServiceException("El proveedor indicado se encuentra eliminado");
                }

                Proveedor proveedorExistente = ((ProveedorRepository) repository)
                        .buscarProveedorPorNombre(proveedor.getNombre());

                boolean proveedorActivo = proveedorExistente != null && !proveedorExistente.isEliminado();

                if (proveedorActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un proveedor con el nombre indicado");
                }

                if (proveedorActivo
                        && useCase == BaseUseCaseService.MODIFICACION
                        && !proveedorExistente.getId().equals(proveedor.getId())) {
                    throw new ErrorServiceException("Existe un proveedor con el nombre indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Override
    public Proveedor alta(Proveedor proveedor) throws ErrorServiceException {
        try {
            // Marcamos como no eliminado
            proveedor.setEliminado(false);

            // Validaciones genéricas
            validar(BaseUseCaseService.ALTA, proveedor);

            // Pre-alta genérico
            preAlta(proveedor);

            // --- Geocodificación ---
            if (proveedor.getDireccion() != null && !proveedor.getDireccion().isEmpty()) {
                double[] coords = geocodingService.obtenerCoordenadas(proveedor.getDireccion());
                proveedor.setLatitud(coords[0]);
                proveedor.setLongitud(coords[1]);
            }

            // Guardar en base
            Proveedor guardado = repository.save(proveedor);

            // Post-alta genérico
            postAlta(guardado);

            return guardado;

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas", e);
        }
    }
}
 
