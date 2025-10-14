package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.AutorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AutorService extends BaseService<Autor> {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        super(autorRepository);
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor crearAutor(Autor autor) throws ErrorServiceException {
        return alta(autor);
    }

    @Transactional
    public Autor modificarAutor(Long id, Autor datosActualizados) throws ErrorServiceException {
        return modificar(id, datosActualizados)
                .orElseThrow(() -> new ErrorServiceException("Autor no encontrado con id " + id));
    }

    @Transactional
    public void eliminarAutor(Long id) throws ErrorServiceException {
        baja(id);
    }

    @Transactional(readOnly = true)
    public List<Autor> listarActivos() throws ErrorServiceException {
        return super.listarActivos();
    }

    @Transactional(readOnly = true)
    public Optional<Autor> buscarPorId(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del autor no puede ser nulo.");
        }
        return obtener(id);
    }

    @Transactional(readOnly = true)
    public List<Autor> buscarPorIds(List<Long> ids) throws ErrorServiceException {
        if (ids == null || ids.isEmpty()) {
            throw new ErrorServiceException("Debe indicar los identificadores de los autores.");
        }
        return autorRepository.findAllById(ids).stream()
                .filter(autor -> !autor.isEliminado())
                .toList();
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Autor entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("El autor no puede ser nulo.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new ErrorServiceException("El nombre del autor es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getApellido())) {
            throw new ErrorServiceException("El apellido del autor es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getBiografia())) {
            throw new ErrorServiceException("La biografía del autor es obligatoria.");
        }
    }
}
