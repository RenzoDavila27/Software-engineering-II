package com.tienda.app.business.logic.service;

import org.springframework.stereotype.Service;

import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.ArticuloRepository;

@Service
public class ArticuloService extends BaseService<Articulo, Long> {

    public ArticuloService(ArticuloRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Articulo articulo) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (articulo == null) {
                    throw new ErrorServiceException("Debe indicar el artículo");
                }

                if (articulo.getNombre() == null || articulo.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre del artículo");
                }

                if (articulo.getPrecio() == null || articulo.getPrecio() <= 0) {
                    throw new ErrorServiceException("Debe indicar un precio válido para el artículo");
                }

                if (articulo.getProveedor() == null || articulo.getProveedor().isEliminado()) {
                    throw new ErrorServiceException("El proveedor indicado es incorrecto");
                }

                if (articulo.isEliminado()) {
                    throw new ErrorServiceException("El artículo indicado se encuentra eliminado");
                }

                Articulo articuloExistente = ((ArticuloRepository) repository).buscarArticuloPorNombre(articulo.getNombre());
                boolean articuloActivo = articuloExistente != null && !articuloExistente.isEliminado();

                if (articuloActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un artículo con el nombre indicado");
                }

                if (articuloActivo && useCase == BaseUseCaseService.MODIFICACION
                        && !articuloExistente.getId().equals(articulo.getId())) {
                    throw new ErrorServiceException("Existe un artículo con el nombre indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
