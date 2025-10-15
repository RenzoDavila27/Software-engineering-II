package com.tienda.app.business.logic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tienda.app.business.domain.Imagen;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.ImagenRepository;

@Service
public class ImagenService extends BaseService<Imagen, Long> {

    public ImagenService(ImagenRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Imagen imagen) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (imagen == null) {
                    throw new ErrorServiceException("Debe indicar la imagen");
                }

                if (imagen.getNombre() == null || imagen.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre de la imagen");
                }

                if (imagen.getMime() == null || imagen.getMime().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el tipo de contenido de la imagen");
                }

                if (imagen.getContenido() == null || imagen.getContenido().length == 0) {
                    throw new ErrorServiceException("Debe indicar el contenido de la imagen");
                }

                if (imagen.getArticulo() == null || imagen.getArticulo().isEliminado()) {
                    throw new ErrorServiceException("Debe indicar un artículo válido para la imagen");
                }

                if (imagen.isEliminado()) {
                    throw new ErrorServiceException("La imagen indicada se encuentra eliminada");
                }

                Imagen imagenExistente = ((ImagenRepository) repository)
                        .buscarImagenPorNombre(imagen.getNombre());

                boolean imagenActiva = imagenExistente != null && !imagenExistente.isEliminado();

                if (imagenActiva && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe una imagen con el nombre indicado");
                }

                if (imagenActiva
                        && useCase == BaseUseCaseService.MODIFICACION
                        && !imagenExistente.getId().equals(imagen.getId())) {
                    throw new ErrorServiceException("Existe una imagen con el nombre indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public List<Imagen> listarActivasPorArticulo(Long articuloId) throws ErrorServiceException {
        try {
            return ((ImagenRepository) repository).findAllByArticuloIdAndEliminadoFalse(articuloId);
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
