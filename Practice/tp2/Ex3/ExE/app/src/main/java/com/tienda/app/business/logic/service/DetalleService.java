package com.tienda.app.business.logic.service;

import org.springframework.stereotype.Service;

import com.tienda.app.business.domain.Detalle;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.DetalleRepository;

@Service
public class DetalleService extends BaseService<Detalle, Long> {

    public DetalleService(DetalleRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Detalle detalle) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (detalle == null) {
                    throw new ErrorServiceException("Debe indicar el detalle");
                }

                if (detalle.getArticulo() == null || detalle.getArticulo().isEliminado()) {
                    throw new ErrorServiceException("El artículo indicado es incorrecto");
                }

                if (detalle.getImagen() == null || detalle.getImagen().isEliminado()) {
                    throw new ErrorServiceException("La imagen indicada es incorrecta");
                }

                if (detalle.isEliminado()) {
                    throw new ErrorServiceException("El detalle indicado se encuentra eliminado");
                }

                if (detalle.getArticulo().getId() == null) {
                    throw new ErrorServiceException("Debe indicar el identificador del artículo asociado");
                }

                if (detalle.getImagen().getId() == null) {
                    throw new ErrorServiceException("Debe indicar el identificador de la imagen asociada");
                }

                Detalle detalleExistente = ((DetalleRepository) repository)
                        .buscarDetallePorArticuloEImagen(detalle.getArticulo().getId(), detalle.getImagen().getId());

                boolean detalleActivo = detalleExistente != null && !detalleExistente.isEliminado();

                if (detalleActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException(
                            "Ya existe un detalle para la combinación de artículo e imagen indicada");
                }

                if (detalleActivo
                        && useCase == BaseUseCaseService.MODIFICACION
                        && !detalleExistente.getId().equals(detalle.getId())) {
                    throw new ErrorServiceException(
                            "Ya existe un detalle para la combinación de artículo e imagen indicada");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
