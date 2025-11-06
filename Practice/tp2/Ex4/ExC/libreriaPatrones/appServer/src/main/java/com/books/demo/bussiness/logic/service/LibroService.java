package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.iterator.LibroIterator;
import com.books.demo.bussiness.logic.iterator.LibroPorAutorIterator;
import com.books.demo.bussiness.logic.strategy.libro.LibroBusquedaStrategy;
import com.books.demo.bussiness.logic.strategy.libro.LibroBusquedaTipo;
import com.books.demo.bussiness.persistance.LibroRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LibroService extends BaseService<Libro> {

    private final LibroRepository libroRepository;
    private final PersonaService personaService;
    private final AutorService autorService;
    private final Map<LibroBusquedaTipo, LibroBusquedaStrategy> estrategiasBusqueda;

    public LibroService(LibroRepository libroRepository,
                        PersonaService personaService,
                        AutorService autorService,
                        List<LibroBusquedaStrategy> estrategiasBusqueda) {
        super(libroRepository);
        this.libroRepository = libroRepository;
        this.personaService = personaService;
        this.autorService = autorService;
        this.estrategiasBusqueda = estrategiasBusqueda == null
                ? Map.of()
                : Map.copyOf(estrategiasBusqueda.stream()
                        .collect(Collectors.toMap(LibroBusquedaStrategy::getTipo, Function.identity(), (a, b) -> a)));
    }

    @Transactional
    public Libro crearLibro(Libro libro) throws ErrorServiceException {
        return alta(libro);
    }

    @Transactional
    public Libro modificarLibro(Long id, Libro datosActualizados) throws ErrorServiceException {
        return modificar(id, datosActualizados)
                .orElseThrow(() -> new ErrorServiceException("Libro no encontrado con id " + id));
    }

    @Transactional
    public void eliminarLibro(Long id) throws ErrorServiceException {
        baja(id);
    }

    @Transactional(readOnly = true)
    public List<Libro> listarActivos() throws ErrorServiceException {
        return super.listarActivos();
    }

    @Transactional(readOnly = true)
    public Optional<Libro> buscarPorId(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del libro no puede ser nulo.");
        }
        return obtener(id);
    }

    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosSinAsignar() throws ErrorServiceException {
        try {
            return libroRepository.buscarLibrosSinAsignar();
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar libros sin asignar.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosAsignados() throws ErrorServiceException {
        try {
            return libroRepository.buscarLibrosAsignados();
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar libros asignados.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> recorrerLibrosPorAutor(Long autorId) throws ErrorServiceException {
        if (autorId == null) {
            throw new ErrorServiceException("Debe indicar el identificador del autor para utilizar el iterador.");
        }
        List<Libro> librosActivos = libroRepository.listarLibrosActivos();
        return filtrarLibrosPorAutor(librosActivos, autorId);
    }

    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosPor(LibroBusquedaTipo tipo, String criterio) throws ErrorServiceException {
        if (tipo == null) {
            throw new ErrorServiceException("Debe indicar el tipo de búsqueda.");
        }
        if (!StringUtils.hasText(criterio)) {
            throw new ErrorServiceException("El criterio de búsqueda no puede ser vacío.");
        }
        if (tipo == LibroBusquedaTipo.AUTOR) {
            return buscarLibrosPorAutorConIterador(criterio);
        }
        LibroBusquedaStrategy estrategia = estrategiasBusqueda.get(tipo);
        if (estrategia == null) {
            throw new ErrorServiceException("No existe una estrategia configurada para el tipo de búsqueda " + tipo);
        }
        try {
            return estrategia.buscar(criterio);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al ejecutar la búsqueda de libros.", e);
        }
    }

    @Transactional(readOnly = true)
    public Persona obtenerPersona(Long personaId) throws ErrorServiceException {
        if (personaId == null) {
            return null;
        }
        return personaService.buscarPorId(personaId)
                .orElseThrow(() -> new ErrorServiceException("Persona no encontrada con id " + personaId));
    }

    @Transactional(readOnly = true)
    public Set<Autor> obtenerAutores(List<Long> autoresIds) throws ErrorServiceException {
        if (autoresIds == null || autoresIds.isEmpty()) {
            throw new ErrorServiceException("Debe indicar al menos un autor para el libro.");
        }
        List<Autor> autores = autorService.buscarPorIds(autoresIds);
        Set<Long> encontrados = autores.stream()
                .map(Autor::getId)
                .collect(Collectors.toSet());
        Set<Long> solicitados = new HashSet<>(autoresIds);
        solicitados.removeAll(encontrados);
        if (!solicitados.isEmpty()) {
            throw new ErrorServiceException("No se encontraron autores con ids: " + solicitados);
        }
        return new HashSet<>(autores);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Libro entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("El libro no puede ser nulo.");
        }
        if (!StringUtils.hasText(entidad.getTitulo())) {
            throw new ErrorServiceException("El título es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getGenero())) {
            throw new ErrorServiceException("El género es obligatorio.");
        }
        if (entidad.getFecha() == null) {
            throw new ErrorServiceException("La fecha es obligatoria.");
        }
        if (entidad.getPaginas() == null || entidad.getPaginas() <= 0) {
            throw new ErrorServiceException("Las páginas deben ser mayores a cero.");
        }
        if (entidad.getTipo() == null) {
            throw new ErrorServiceException("El tipo de libro es obligatorio.");
        }
        Collection<?> autores = entidad.getAutores();
        if (autores == null || autores.isEmpty()) {
            throw new ErrorServiceException("El libro debe tener al menos un autor.");
        }
    }

    @Override
    protected void preAlta(Libro entidad) throws ErrorServiceException {
        entidad.setPersona(obtenerPersonaPersistente(entidad.getPersona()));
        entidad.setAutores(obtenerAutoresPersistentes(entidad.getAutores()));
        entidad.setEliminado(false);
    }

    @Override
    protected void preModificacion(Libro entidad) throws ErrorServiceException {
        entidad.setPersona(obtenerPersonaPersistente(entidad.getPersona()));
        entidad.setAutores(obtenerAutoresPersistentes(entidad.getAutores()));
    }

    private Persona obtenerPersonaPersistente(Persona persona) throws ErrorServiceException {
        if (persona == null || persona.getId() == null) {
            return null;
        }
        return personaService.buscarPorId(persona.getId())
                .orElseThrow(() -> new ErrorServiceException("Persona no encontrada con id " + persona.getId()));
    }

    private Set<Autor> obtenerAutoresPersistentes(Collection<Autor> autores) throws ErrorServiceException {
        if (autores == null || autores.isEmpty()) {
            throw new ErrorServiceException("Debe informar al menos un autor.");
        }
        List<Long> ids = autores.stream()
                .map(Autor::getId)
                .toList();
        return obtenerAutores(ids);
    }

    private List<Libro> filtrarLibrosPorAutor(List<Libro> librosActivos, Long autorId) throws ErrorServiceException {
        if (autorId == null) {
            return List.of();
        }
        autorService.buscarPorId(autorId)
                .orElseThrow(() -> new ErrorServiceException("Autor no encontrado con id " + autorId));
        LibroIterator iterador = new LibroPorAutorIterator(librosActivos, autorId);
        List<Libro> resultado = new ArrayList<>();
        while (iterador.hasNext()) {
            resultado.add(iterador.next());
        }
        return resultado;
    }

    private List<Libro> buscarLibrosPorAutorConIterador(String criterio) throws ErrorServiceException {
        String valor = criterio.trim();
        LinkedHashSet<Long> autoresIds = new LinkedHashSet<>();
        try {
            autoresIds.add(Long.parseLong(valor));
        } catch (NumberFormatException ignored) {
            // No es un id numérico, se buscará por nombre/apellido.
        }
        autoresIds.addAll(autorService.buscarIdsPorNombreSimilar(valor));
        if (autoresIds.isEmpty()) {
            return List.of();
        }
        List<Libro> librosActivos = libroRepository.listarLibrosActivos();
        LinkedHashSet<Libro> acumulado = new LinkedHashSet<>();
        for (Long autorId : autoresIds) {
            acumulado.addAll(filtrarLibrosPorAutor(librosActivos, autorId));
        }
        return new ArrayList<>(acumulado);
    }
}
