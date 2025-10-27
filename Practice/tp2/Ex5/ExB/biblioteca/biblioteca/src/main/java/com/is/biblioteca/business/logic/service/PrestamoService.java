package com.is.biblioteca.business.logic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.is.biblioteca.business.domain.entity.Libro;
import com.is.biblioteca.business.domain.entity.Prestamo;
import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.persistence.repository.PrestamoRepository;

import jakarta.transaction.Transactional;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    private void validarAlta(String idLibro, String idUsuario) throws ErrorServiceException {
        if (idLibro == null || idLibro.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el libro del préstamo");
        }
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el usuario del préstamo");
        }
    }

    @Transactional
    public Prestamo crearPrestamo(String idLibro, String idUsuario)
            throws ErrorServiceException {
        try {
            validarAlta(idLibro, idUsuario);

            Libro libro = libroService.buscarLibro(idLibro);
            if (libro == null) {
                throw new ErrorServiceException("No se encontró el libro indicado");
            }

            Usuario usuario = usuarioService.buscarUsuario(idUsuario);
            if (usuario == null) {
                throw new ErrorServiceException("No se encontró el usuario indicado");
            }

            if (libro.getEjemplaresRestantes() == null) {
                libro.setEjemplaresRestantes(
                        libro.getEjemplares() != null ? libro.getEjemplares() : 0);
            }
            if (libro.getEjemplaresPrestados() == null) {
                libro.setEjemplaresPrestados(0);
            }

            if (libro.getEjemplaresRestantes() <= 0) {
                throw new ErrorServiceException("No hay ejemplares disponibles para el préstamo");
            }

            Prestamo existente = prestamoRepository.buscarPrestamoActivoPorLibro(libro.getId());
            if (existente != null) {
                throw new ErrorServiceException("El libro indicado ya se encuentra prestado");
            }

            Prestamo prestamo = new Prestamo();
            prestamo.setId(UUID.randomUUID().toString());
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setFechaDevolucion(null);
            prestamo.setAlta(true);
            prestamo.setLibro(libro);
            prestamo.setUsuario(usuario);

            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);

            return prestamoRepository.save(prestamo);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public Prestamo devolverPrestamo(String idPrestamo) throws ErrorServiceException {
        try {
            Prestamo prestamo = buscarPrestamo(idPrestamo);

            if (!prestamo.isAlta()) {
                throw new ErrorServiceException("El préstamo ya fue devuelto");
            }

            prestamo.setAlta(false);
            prestamo.setFechaDevolucion(LocalDate.now());

            Libro libro = prestamo.getLibro();
            if (libro != null) {
                if (libro.getEjemplaresPrestados() == null) {
                    libro.setEjemplaresPrestados(0);
                }
                if (libro.getEjemplaresRestantes() == null) {
                    libro.setEjemplaresRestantes(
                            libro.getEjemplares() != null ? libro.getEjemplares() : 0);
                }
                if (libro.getEjemplaresPrestados() > 0) {
                    libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
                }
                int ejemplaresTotales = libro.getEjemplares() != null ? libro.getEjemplares() : 0;
                int prestados = libro.getEjemplaresPrestados() != null ? libro.getEjemplaresPrestados() : 0;
                libro.setEjemplaresRestantes(Math.max(0, ejemplaresTotales - prestados));
            }

            return prestamoRepository.save(prestamo);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public Prestamo buscarPrestamo(String idPrestamo) throws ErrorServiceException {
        try {
            if (idPrestamo == null || idPrestamo.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el préstamo");
            }

            Optional<Prestamo> optional = prestamoRepository.findById(idPrestamo);
            Prestamo prestamo = null;
            if (optional.isPresent()) {
                prestamo = optional.get();
            } else {
                throw new ErrorServiceException("No se encuentra el préstamo indicado");
            }

            return prestamo;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public List<Prestamo> listarPrestamos() throws ErrorServiceException {
        try {
            return prestamoRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public List<Prestamo> listarPrestamosActivos() throws ErrorServiceException {
        try {
            return prestamoRepository.listarPrestamosActivos();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public List<Prestamo> listarPrestamosPorUsuario(String idUsuario) throws ErrorServiceException {
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el usuario");
            }
            return prestamoRepository.listarPrestamosPorUsuario(idUsuario);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
