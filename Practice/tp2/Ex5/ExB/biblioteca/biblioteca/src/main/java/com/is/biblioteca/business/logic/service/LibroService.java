package com.is.biblioteca.business.logic.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.is.biblioteca.business.domain.entity.Autor;
import com.is.biblioteca.business.domain.entity.Editorial;
import com.is.biblioteca.business.domain.entity.Imagen;
import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.persistence.repository.LibroRepository;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
public class LibroService {

	@Autowired
	private LibroRepository repository;

	@Autowired
	private AutorService autorService;

	@Autowired
	private EditorialService editorialService;

	@Autowired
	private ImagenService imagenService;

	public void validar(Long isbn, String titulo, Integer ejemplares) throws ErrorServiceException {

		try {

			if (isbn == null || isbn.toString().isEmpty()) {
				throw new ErrorServiceException("Debe indicar el isbn");
			}

			if (titulo == null || titulo.isEmpty()) {
				throw new ErrorServiceException("Debe indicar el título");
			}

			if (ejemplares == null || ejemplares < 0) {
				throw new ErrorServiceException(
						"Debe indicar la cantidad de ejemplares. La cantidad no puede ser menor que cero");
			}

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	@Transactional
	public void crearLibro(MultipartFile archivo, Long isbn, String titulo, Integer ejemplares, String idAutor,
			               String idEditorial) throws ErrorServiceException {

		try {

			validar(isbn, titulo, ejemplares);

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
			libro.setAutor(autor);
			libro.setEditorial(editorial);
			libro.setEliminado(false);

			Imagen imagen = imagenService.crearImagen(archivo);
			libro.setImagen(imagen);

			repository.save(libro);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	@Transactional
	public Libro modificarLibro(MultipartFile archivo, String idLibro, Long isbn, String titulo, Integer ejemplares,
								String idAutor, String idEditorial) throws ErrorServiceException {

		try {
			validar(isbn, titulo, ejemplares);

			Autor autor = autorService.buscarAutor(idAutor);

			Editorial editorial = editorialService.buscarEditorial(idEditorial);

			Libro libro = buscarLibro(idLibro);

			try {
				Libro libroAux = repository.buscarLibroPorIsbn(isbn);
				if (libroAux != null && !libroAux.getId().equals(idLibro) && !libroAux.isEliminado()) {
					throw new ErrorServiceException("Existe un libro con el isbn indicado");
				}
			} catch (NoResultException ex) {
			}

			try {
				Libro libroAux = repository.buscarLibroPorTituloAutorEditorial(titulo, idAutor, idEditorial);
				if (libroAux != null && !libroAux.getId().equals(idLibro) && !libroAux.isEliminado()) {
					throw new ErrorServiceException("Existe un libro con el título, autor y editorial indicado");
				}
			} catch (NoResultException ex) {
			}

			libro.setIsbn(isbn);
			libro.setTitulo(titulo);
			libro.setEjemplares(ejemplares);
			libro.setAutor(autor);
			libro.setEditorial(editorial);

			String idImagen = null;
			if (libro.getImagen() != null) {
				idImagen = libro.getImagen().getId();
			}

			Imagen imagen = imagenService.modificarImagen(idImagen, archivo);
			libro.setImagen(imagen);

			return repository.save(libro);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	@Transactional
	public void eliminarLibro(String idLibro) throws ErrorServiceException {

		try {

			Libro libro = buscarLibro(idLibro);
			libro.setEliminado(true);

			repository.save(libro);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	public Libro buscarLibro(String idLibro) throws ErrorServiceException {

		try {

			if (idLibro == null || idLibro.trim().isEmpty()) {
				throw new ErrorServiceException("Debe indicar el libro");
			}

			Optional<Libro> optional = repository.findById(idLibro);
			Libro libro = null;
			if (optional.isPresent()) {
				libro = optional.get();
				if (libro.isEliminado()) {
					throw new ErrorServiceException("No se encuentra el libro indicado");
				}
			}

			return libro;

		} catch (ErrorServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ErrorServiceException("Error de sistema");
		}
	}

	public List<Libro> listarLibro() throws ErrorServiceException {

		try {

			return repository.findAll();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ErrorServiceException("Error de sistema");
		}
	}

	public Libro buscarLibroPorIsbn(Long isbn) throws ErrorServiceException {

		try {

			if (isbn == null || isbn <= 0) {
				throw new ErrorServiceException("Debe indicar el isbn");
			}

			return repository.buscarLibroPorIsbn(isbn);

		} catch (ErrorServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ErrorServiceException("Error de sistema");
		}
	}

	public Libro buscarLibroPorTitulo(String titulo) throws ErrorServiceException {

		try {

			if (titulo == null || titulo.trim().isEmpty()) {
				throw new ErrorServiceException("Debe indicar el título");
			}

			return repository.buscarLibroPorTitulo(titulo);

		} catch (ErrorServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ErrorServiceException("Error de sistema");
		}
	}

	public List<Libro> listarLibroPorEditorial(String nombre) throws ErrorServiceException {

		try {

			if (nombre == null || nombre.trim().isEmpty()) {
				throw new ErrorServiceException("Debe indicar el nombre");
			}

			return repository.listarLibroPorEditorial(nombre);

		} catch (ErrorServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ErrorServiceException("Error de sistema");
		}
	}

	public List<Libro> listarLibroPorFiltro(String filtro) throws ErrorServiceException {

		try {

			if (filtro == null || filtro.trim().isEmpty()) {
				throw new ErrorServiceException("Debe indicar el criterio de búsqueda");
			}

			return repository.listarLibroPorFiltro(filtro);

		} catch (ErrorServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ErrorServiceException("Error de sistema");
		}
	}
}
