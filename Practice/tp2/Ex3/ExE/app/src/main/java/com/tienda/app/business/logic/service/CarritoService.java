package com.tienda.app.business.logic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.domain.Carrito;
import com.tienda.app.business.domain.CarritoItem;
import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.persistence.repository.CarritoItemRepository;
import com.tienda.app.business.persistence.repository.CarritoRepository;

@Service
public class CarritoService extends BaseService<Carrito, Long> {

    private final CarritoItemRepository itemRepository;

    public CarritoService(CarritoRepository repository, CarritoItemRepository itemRepository) {
        super(repository);
        this.itemRepository = itemRepository;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Carrito carrito) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {

                if (carrito == null) {
                    throw new ErrorServiceException("Debe indicar el carrito");
                }

                if (carrito.getTotal() == null || carrito.getTotal() < 0) {
                    throw new ErrorServiceException("Debe indicar un total válido para el carrito");
                }

                if (carrito.getUsuario() == null || carrito.getUsuario().isEliminado()) {
                    throw new ErrorServiceException("El usuario del carrito indicado es incorrecto");
                }

                if (carrito.isEliminado()) {
                    throw new ErrorServiceException("El carrito indicado se encuentra eliminado");
                }

                if (carrito.getUsuario().getId() == null) {
                    throw new ErrorServiceException("Debe indicar el identificador del usuario asociado");
                }

                Carrito carritoExistente = ((CarritoRepository) repository)
                        .buscarCarritoActivoPorUsuario(carrito.getUsuario().getId());

                boolean carritoActivo = carritoExistente != null && !carritoExistente.isEliminado();

                if (carritoActivo && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("El usuario indicado ya posee un carrito activo");
                }

                if (carritoActivo
                        && useCase == BaseUseCaseService.MODIFICACION
                        && !carritoExistente.getId().equals(carrito.getId())) {
                    throw new ErrorServiceException("El usuario indicado ya posee un carrito activo");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public Carrito obtenerOCrearCarrito(Usuario usuario) throws ErrorServiceException {
        if (usuario == null || usuario.getId() == null) {
            throw new ErrorServiceException("Debe indicar el usuario.");
        }
        Carrito carritoExistente = ((CarritoRepository) repository).buscarCarritoActivoPorUsuario(usuario.getId());
        if (carritoExistente != null) {
            return carritoExistente;
        }
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setTotal(0d);
        return repository.save(carrito);
    }

    @Transactional
    public void agregarArticuloAlCarrito(Usuario usuario, Articulo articulo, int cantidad) throws ErrorServiceException {
        if (articulo == null || articulo.getId() == null) {
            throw new ErrorServiceException("Debe indicar el artículo a agregar.");
        }
        if (cantidad <= 0) {
            cantidad = 1;
        }
        Carrito carrito = obtenerOCrearCarrito(usuario);
        CarritoItem item = itemRepository.buscarItemPorCarritoYArticulo(carrito.getId(), articulo.getId());

        if (item == null) {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setArticulo(articulo);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(articulo.getPrecio() != null ? articulo.getPrecio() : 0d);
            item.setEliminado(false);
        } else {
            int cantidadActual = item.getCantidad() != null ? item.getCantidad() : 0;
            item.setCantidad(cantidadActual + cantidad);
        }

        carrito.agregarItem(item);
        itemRepository.save(item);
        recalcularTotal(carrito);
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> listarItems(Usuario usuario) throws ErrorServiceException {
        Carrito carrito = obtenerOCrearCarrito(usuario);
        return itemRepository.listarPorCarrito(carrito.getId());
    }

    @Transactional
    public void eliminarItem(Long itemId, Usuario usuario) throws ErrorServiceException {
        if (itemId == null) {
            throw new ErrorServiceException("Debe indicar el item a eliminar.");
        }
        Carrito carrito = obtenerOCrearCarrito(usuario);
        CarritoItem item = itemRepository.findById(itemId)
                                         .filter(ci -> !Boolean.TRUE.equals(ci.isEliminado()))
                                         .orElseThrow(() -> new ErrorServiceException("El item indicado no existe."));
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new ErrorServiceException("El item indicado no pertenece al carrito del usuario.");
        }
        item.setEliminado(true);
        itemRepository.save(item);
        recalcularTotal(carrito);
    }

    @Transactional
    public void vaciarCarrito(Usuario usuario) throws ErrorServiceException {
        Carrito carrito = obtenerOCrearCarrito(usuario);
        List<CarritoItem> items = itemRepository.listarPorCarrito(carrito.getId());
        for (CarritoItem item : items) {
            item.setEliminado(true);
            itemRepository.save(item);
        }
        carrito.setTotal(0d);
        repository.save(carrito);
    }

    @Transactional(readOnly = true)
    public Double obtenerTotal(Usuario usuario) throws ErrorServiceException {
        Carrito carrito = obtenerOCrearCarrito(usuario);
        return carrito.getTotal() != null ? carrito.getTotal() : 0d;
    }

    private void recalcularTotal(Carrito carrito) {
        List<CarritoItem> itemsActuales = itemRepository.listarPorCarrito(carrito.getId());
        double total = itemsActuales.stream()
                                    .filter(ci -> !Boolean.TRUE.equals(ci.isEliminado()))
                                    .mapToDouble(ci -> {
                                        double precio = ci.getPrecioUnitario() != null ? ci.getPrecioUnitario() : 0d;
                                        int cantidad = ci.getCantidad() != null ? ci.getCantidad() : 0;
                                        return precio * cantidad;
                                    })
                                    .sum();
        carrito.setTotal(total);
        repository.save(carrito);
    }
}
