package com.example.demo.business.logic.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.domain.Videojuego;
import com.example.demo.business.domain.Categoria;
import com.example.demo.business.domain.Estudio;
import com.example.demo.business.logic.error.ErrorServiceException;
import com.example.demo.business.persistence.repository.VideojuegoRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class VideojuegoService {
    
    @Autowired
    private  VideojuegoRepository repository;

    @Transactional
    public void crearVideojuego(String titulo, String rutaimg, float precio, Short cantidad, String descripcion,
     Boolean oferta,String fechalanzamiento,Categoria categoria, Estudio estudio) throws ErrorServiceException{
        try{
            validar(titulo);
            try {
            	Videojuego VideojuegoAux = repository.buscarVideojuegoPorNombre(titulo);
            	if (VideojuegoAux != null && !VideojuegoAux.getActivo()) {
                 throw new ErrorServiceException("Existe un videojuego con el nombre indicado");
            	} 
            } catch (NoResultException ex) {}

            Videojuego juego = new Videojuego();
            juego.setTitulo(titulo);
            juego.setRutaimg(rutaimg);
            juego.setPrecio(precio);
            juego.setCantidad(cantidad);
            juego.setDescripcion(descripcion);
            juego.setOferta(oferta);
            juego.setFechalanzamiento(fechalanzamiento);
            juego.setActivo(true);
            juego.setCategoria(categoria);
            juego.setEstudio(estudio);
            repository.save(juego);
            


        } catch (ErrorServiceException e){
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public void validar(String nombre)throws ErrorServiceException {
        
        try{
            
            if (nombre == null || nombre.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public void editarVideojuego(Long id,String titulo, String rutaimg, float precio, Short cantidad, String descripcion,
        Boolean oferta,String fechalanzamiento, Categoria categoria, Estudio estudio) throws ErrorServiceException {
        try{
            
            Videojuego videojuego = buscarVideojuego(id);
            if (titulo != null && !titulo.isBlank()) {
                videojuego.setTitulo(titulo);
            }
            if (rutaimg != null && !rutaimg.isBlank()) {
                videojuego.setRutaimg(rutaimg);
            }
            if (precio > 0) {
                videojuego.setPrecio(precio);
            }
            if (cantidad != null) {
                videojuego.setCantidad(cantidad);
            }
            if (descripcion != null && !descripcion.isBlank()) {
                videojuego.setDescripcion(descripcion);
            }
            if (oferta != null) {
                videojuego.setOferta(oferta);
            }
            if (fechalanzamiento != null && !fechalanzamiento.isBlank()) {
                videojuego.setFechalanzamiento(fechalanzamiento);
            }
            videojuego.setCategoria(categoria);
            videojuego.setEstudio(estudio);
            repository.save(videojuego);

        } catch (ErrorServiceException e){
            throw e; // la vuelvo a lanzar si quiero
        } catch (Exception ex){
            ex.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Videojuego buscarVideojuego(Long id) throws ErrorServiceException {
        try {
            if (id == null) {
                throw new ErrorServiceException("Debe indicar la categoría");
            }

            var optional = repository.findById(id);
            if (optional.isEmpty() || !optional.get().getActivo()) {
                throw new ErrorServiceException("No se encuentra la categoría indicada");
            }

            return optional.get();

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    @Transactional
    public Collection<Videojuego> buscarVideojuegoPorNombre(String nombre) throws ErrorServiceException {
        try {
            if (nombre == null || nombre.isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }

            Collection<Videojuego> listaVideojuego = List.of(repository.buscarVideojuegoPorNombre(nombre));
            return listaVideojuego;

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }


    @Transactional
    public void eliminarVideojuego(Long id) throws ErrorServiceException {  

        try {

            Videojuego videojuego = buscarVideojuego(id);
            videojuego.setActivo(false);
            
            repository.save(videojuego);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public Collection<Videojuego> listarVideojuego() throws ErrorServiceException {
        try {
            
        	/* findAll(): Este método de JpaRepository
             * se utiliza para obtener todas las entidades del tipo E desde la base de datos.
             * Devuelve una lista (List<E>) de todas las entidades.
             */
        	
            return repository.findAll();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    public Collection<Videojuego> listarVideojuegoActivo() throws ErrorServiceException {
        try {
            
            return repository.listarVideojuegoActivo();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }


}
