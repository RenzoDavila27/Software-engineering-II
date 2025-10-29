package com.is.biblioteca.business.logic.service.facade;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.NoResultException; // Para capturar excepciones de consultas sin resultados

import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.domain.entity.Autor;
import com.is.biblioteca.business.domain.entity.Editorial;
import com.is.biblioteca.business.domain.entity.Imagen;

import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.service.AutorService;
import com.is.biblioteca.business.logic.service.EditorialService;
import com.is.biblioteca.business.logic.service.ImagenService;
import com.is.biblioteca.business.persistence.repository.LibroRepository;

@Component
public class FachadaCrearLibro {

    @Autowired
    private AutorService autorService;

    @Autowired
    private EditorialService editorialService;

    @Autowired 
    private ImagenService imagenService;

    @Autowired
    private LibroRepository repository;
    
    public Libro validarFacade(MultipartFile archivo, Long isbn, String titulo, Integer ejemplares, String idAutor,
			               String idEditorial) throws ErrorServiceException{

            Autor autor = autorService.buscarAutor(idAutor);

			Editorial editorial = editorialService.buscarEditorial(idEditorial);

			try {
				Libro libroAux = repository.buscarLibroPorIsbn(isbn);
				if (libroAux != null && !libroAux.isEliminado()) {
					throw new ErrorServiceException("Existe un libro con el isbn indicado");
				}
			} catch (NoResultException ex) {}

			try {
				Libro libroAux = repository.buscarLibroPorTituloAutorEditorial(titulo, idAutor, idEditorial);
				if (libroAux != null && !libroAux.isEliminado()) {
					throw new ErrorServiceException("Existe un libro con el título, autor y editorial indicado");
				}
			} catch (NoResultException ex) {}

			Libro libro = new Libro();
			libro.setId(UUID.randomUUID().toString());
			libro.setIsbn(isbn);
			libro.setTitulo(titulo);
			libro.setEjemplares(ejemplares);
			libro.setEjemplaresPrestados(0);
			libro.setEjemplaresRestantes(ejemplares);
			libro.setAutor(autor);
			libro.setEditorial(editorial);
			libro.setEliminado(false);

			Imagen imagen = imagenService.crearImagen(archivo);
			libro.setImagen(imagen);
            return libro;
        
            
    }
}
