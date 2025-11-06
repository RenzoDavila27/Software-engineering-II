package com.example.demo.business.logic.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.business.domain.Videojuego;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.VideojuegoRepository;
import com.example.demo.controller.api.dto.VideojuegoRequest;

@Service
public class VideojuegoService extends BaseService<Videojuego> {

    private final VideojuegoRepository repository;
    private final CategoriaService categoriaService;
    private final EstudioService estudioService;

    public VideojuegoService(VideojuegoRepository repository,
                             CategoriaService categoriaService,
                             EstudioService estudioService) {
        super(repository);
        this.repository = repository;
        this.categoriaService = categoriaService;
        this.estudioService = estudioService;
    }

    public Collection<Videojuego> listarActivosDesdeConsulta() throws ErrorServiceException {
        try {
            return repository.listarVideojuegoActivo();
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Collection<Videojuego> buscarPorNombre(String nombre) throws ErrorServiceException {
        try {
            if (nombre == null || nombre.isBlank()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            Videojuego videojuego = repository.buscarVideojuegoPorNombre(nombre);
            return videojuego == null ? List.of() : List.of(videojuego);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public void crearDesdeRequest(VideojuegoRequest request) throws ErrorServiceException {
        Videojuego videojuego = buildFromRequest(request);
        crear(videojuego);
    }

    public void actualizarDesdeRequest(Long id, VideojuegoRequest request) throws ErrorServiceException {
        Videojuego datos = buildFromRequest(request);
        actualizar(id, datos);
    }

    @Override
    protected void validar(Videojuego entidad) throws ErrorServiceException {
        if (entidad.getTitulo() == null || entidad.getTitulo().isBlank()) {
            throw new ErrorServiceException("Debe indicar el título");
        }
        if (entidad.getPrecio() <= 0) {
            throw new ErrorServiceException("Debe indicar un precio válido");
        }
        if (entidad.getCantidad() == null || entidad.getCantidad() < 0) {
            throw new ErrorServiceException("Debe indicar la cantidad disponible");
        }
        if (entidad.getCategoria() == null || entidad.getCategoria().getId() == null) {
            throw new ErrorServiceException("Debe indicar la categoría");
        }
        if (entidad.getEstudio() == null || entidad.getEstudio().getId() == null) {
            throw new ErrorServiceException("Debe indicar el estudio");
        }
    }

    @Override
    protected void preCrear(Videojuego entidad) throws ErrorServiceException {
        Videojuego existente = repository.buscarVideojuegoPorNombre(entidad.getTitulo());
        if (existente != null && !Boolean.TRUE.equals(existente.getEliminado())) {
            throw new ErrorServiceException("Existe un videojuego con el título indicado");
        }
    }

    @Override
    protected void preActualizar(Videojuego existente, Videojuego datos) throws ErrorServiceException {
        if (datos.getTitulo() != null && !datos.getTitulo().equalsIgnoreCase(existente.getTitulo())) {
            Videojuego otro = repository.buscarVideojuegoPorNombre(datos.getTitulo());
            if (otro != null && !Boolean.TRUE.equals(otro.getEliminado()) && !otro.getId().equals(existente.getId())) {
                throw new ErrorServiceException("Existe un videojuego con el título indicado");
            }
        }
    }

    @Override
    protected void copiarPropiedades(Videojuego origen, Videojuego destino) throws ErrorServiceException {
        if (origen.getTitulo() != null) {
            destino.setTitulo(origen.getTitulo());
        }
        if (origen.getRutaimg() != null) {
            destino.setRutaimg(origen.getRutaimg());
        }
        if (origen.getPrecio() > 0) {
            destino.setPrecio(origen.getPrecio());
        }
        if (origen.getCantidad() != null) {
            destino.setCantidad(origen.getCantidad());
        }
        if (origen.getDescripcion() != null) {
            destino.setDescripcion(origen.getDescripcion());
        }
        if (origen.getOferta() != null) {
            destino.setOferta(origen.getOferta());
        }
        if (origen.getFechalanzamiento() != null) {
            destino.setFechalanzamiento(origen.getFechalanzamiento());
        }
        if (origen.getCategoria() != null) {
            destino.setCategoria(origen.getCategoria());
        }
        if (origen.getEstudio() != null) {
            destino.setEstudio(origen.getEstudio());
        }
    }

    private Videojuego buildFromRequest(VideojuegoRequest request) throws ErrorServiceException {
        Videojuego videojuego = new Videojuego();
        videojuego.setTitulo(request.titulo());
        videojuego.setRutaimg(request.rutaimg());
        videojuego.setPrecio(request.precio());
        videojuego.setCantidad(request.cantidad());
        videojuego.setDescripcion(request.descripcion());
        videojuego.setOferta(request.oferta());
        videojuego.setFechalanzamiento(request.fechalanzamiento());
        videojuego.setCategoria(categoriaService.obtenerActivo(request.categoriaId()));
        videojuego.setEstudio(estudioService.obtenerActivo(request.estudioId()));
        return videojuego;
    }
}
