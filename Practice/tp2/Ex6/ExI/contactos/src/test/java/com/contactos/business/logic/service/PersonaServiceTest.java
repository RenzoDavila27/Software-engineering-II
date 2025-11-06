package com.contactos.business.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.contactos.business.domain.Persona;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.PersonaRepository;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    private PersonaService personaService;

    @BeforeEach
    void setUp() {
        personaService = new PersonaService(personaRepository);
    }

    @Test
    void listarActivasConRelaciones_filtroEliminadas() throws ErrorServiceException {
        Persona activaBasica = new Persona();
        activaBasica.setId(1L);
        activaBasica.setNombre("Ana");
        activaBasica.setApellido("Lopez");
        activaBasica.setEliminado(false);

        Persona activaCompleta = new Persona();
        activaCompleta.setId(1L);
        activaCompleta.setNombre("Ana");
        activaCompleta.setApellido("Lopez");
        activaCompleta.setEliminado(false);

        Persona eliminada = new Persona();
        eliminada.setId(2L);
        eliminada.setNombre("Juan");
        eliminada.setApellido("Perez");
        eliminada.setEliminado(true);

        when(personaRepository.findAllActivas())
                .thenReturn(List.of(activaBasica, eliminada));
        when(personaRepository.findWithRelationshipsById(eq(1L)))
                .thenReturn(Optional.of(activaCompleta));

        List<Persona> personas = personaService.listarActivasConRelaciones();

        assertThat(personas)
                .containsExactly(activaCompleta);
    }
}
