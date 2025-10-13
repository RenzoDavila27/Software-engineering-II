package com.books.demo.bussiness.logic;

import com.books.demo.client.dto.PersonaDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.PersonaRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public List<PersonaDto> listarPersonas() {
        return personaRepository.findAll().stream()
                .filter(persona -> persona != null && StringUtils.hasText(persona.getNombre()))
                .sorted(Comparator.comparing(PersonaDto::getNombre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(persona -> persona.getApellido() == null ? "" : persona.getApellido(),
                                String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<PersonaDto> obtenerPersona(Long id) {
        return personaRepository.findById(id);
    }

    public PersonaDto crearPersona(PersonaDto persona) {
        validarPersona(persona);
        persona.setId(null);
        return personaRepository.save(persona);
    }

    public PersonaDto actualizarPersona(Long id, PersonaDto persona) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la persona es obligatorio.");
        }
        validarPersona(persona);
        persona.setId(id);
        return personaRepository.update(id, persona)
                .orElseThrow(() -> new ApiClientException(
                        "La API no devolvio datos al actualizar la persona con id " + id + "."));
    }

    public void eliminarPersona(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la persona es obligatorio.");
        }
        personaRepository.deleteById(id);
    }

    private void validarPersona(PersonaDto persona) {
        if (persona == null) {
            throw new IllegalArgumentException("Los datos de la persona no pueden ser nulos.");
        }
        if (!StringUtils.hasText(persona.getNombre())) {
            throw new IllegalArgumentException("El nombre de la persona es obligatorio.");
        }
        if (!StringUtils.hasText(persona.getApellido())) {
            throw new IllegalArgumentException("El apellido de la persona es obligatorio.");
        }
        if (persona.getDni() != null && persona.getDni() <= 0) {
            throw new IllegalArgumentException("El DNI debe ser un numero positivo.");
        }
    }
}
