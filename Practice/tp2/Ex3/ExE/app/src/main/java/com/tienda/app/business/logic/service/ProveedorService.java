package com.tienda.app.business.logic.service;

import org.springframework.stereotype.Service;

import com.tienda.app.business.domain.Proveedor;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.ProveedorRepository;

@Service
public class ProveedorService extends BaseService<Proveedor, Long> {

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
}
