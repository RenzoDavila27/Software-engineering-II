package com.car.clientead.business.logic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.PersonaDto;
import com.car.clientead.repository.ContactoCorreoElectronicoRepository;
import com.car.clientead.repository.ContactoTelefonicoRepository;
import com.car.clientead.repository.DireccionRepository;
import com.car.clientead.repository.ImagenRepository;
import com.car.clientead.repository.PersonaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersonaService {

    @Autowired
    private PersonaRepository repository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private ContactoTelefonicoRepository contactoTelefonicoRepository;

    @Autowired
    private ContactoCorreoElectronicoRepository contactoCorreoRepository;

    public List<PersonaDto> listar() {
        return repository.findAll().stream()
                .filter(this::personaValida)
                .collect(Collectors.toList());
    }

    public PersonaDto consultar(String id) {
        return repository.findById(id);
    }

    public PersonaDto crearConDatosRelacionados(PersonaDto persona,
                                                DireccionDto direccion,
                                                ImagenDto imagen,
                                                ContactoTelefonicoDto contactoTelefonico,
                                                ContactoCorreoElectronicoDto contactoCorreo,
                                                String tipoContactoPreferido) {
        validarPersonaBase(persona);
        Deque<Runnable> rollbacks = new ArrayDeque<>();
        try {
            if (!StringUtils.hasText(persona.getDireccionId())) {
                DireccionDto creada = crearDireccion(direccion);
                persona.setDireccionId(creada.getId());
                rollbacks.push(() -> eliminarDireccionSeguro(creada.getId()));
            }
            if (!StringUtils.hasText(persona.getImagenId())) {
                ImagenDto creada = crearImagen(imagen);
                persona.setImagenId(creada.getId());
                rollbacks.push(() -> eliminarImagenSeguro(creada.getId()));
            }
            if (!StringUtils.hasText(persona.getContactoId())) {
                boolean usarCorreo = usaContactoCorreo(tipoContactoPreferido);
                if (usarCorreo) {
                    ContactoCorreoElectronicoDto creado = crearContactoCorreo(contactoCorreo);
                    persona.setContactoId(creado.getId());
                    rollbacks.push(() -> eliminarContactoCorreoSeguro(creado.getId()));
                } else {
                    ContactoTelefonicoDto creado = crearContactoTelefonico(contactoTelefonico);
                    persona.setContactoId(creado.getId());
                    rollbacks.push(() -> eliminarContactoTelefonicoSeguro(creado.getId()));
                }
            }
            validarPersonaCompleta(persona);
            return repository.create(persona);
        } catch (RuntimeException ex) {
            ejecutarRollbacks(rollbacks);
            throw ex;
        }
    }

    public void eliminarConRelaciones(String personaId) {
        if (!StringUtils.hasText(personaId)) {
            return;
        }
        PersonaDto persona = null;
        try {
            persona = repository.findById(personaId);
        } catch (RuntimeException ex) {
            log.warn("No se pudo consultar la persona {} antes de eliminarla: {}", personaId, ex.getMessage());
        }
        try {
            repository.delete(personaId);
        } catch (RuntimeException ex) {
            log.warn("No se pudo eliminar la persona {}: {}", personaId, ex.getMessage());
        }
        if (persona == null) {
            return;
        }
        eliminarDireccionSeguro(persona.getDireccionId());
        eliminarImagenSeguro(persona.getImagenId());
        eliminarContactoTelefonicoSeguro(persona.getContactoId());
        eliminarContactoCorreoSeguro(persona.getContactoId());
    }

    private DireccionDto crearDireccion(DireccionDto dto) {
        validarDireccionNueva(dto);
        return direccionRepository.create(dto);
    }

    private ImagenDto crearImagen(ImagenDto dto) {
        validarImagenNueva(dto);
        return imagenRepository.create(dto);
    }

    private ContactoTelefonicoDto crearContactoTelefonico(ContactoTelefonicoDto dto) {
        validarContactoTelefonicoNuevo(dto);
        return contactoTelefonicoRepository.create(dto);
    }

    private ContactoCorreoElectronicoDto crearContactoCorreo(ContactoCorreoElectronicoDto dto) {
        validarContactoCorreoNuevo(dto);
        return contactoCorreoRepository.create(dto);
    }

    private void ejecutarRollbacks(Deque<Runnable> rollbacks) {
        while (!rollbacks.isEmpty()) {
            Runnable rollback = rollbacks.pop();
            try {
                rollback.run();
            } catch (RuntimeException ex) {
                log.warn("No se pudo revertir una operación auxiliar: {}", ex.getMessage());
            }
        }
    }

    private void eliminarDireccionSeguro(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        try {
            direccionRepository.delete(id);
        } catch (RuntimeException ex) {
            log.warn("No se pudo eliminar la dirección {} al revertir el alta de la persona: {}", id, ex.getMessage());
        }
    }

    private void eliminarImagenSeguro(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        try {
            imagenRepository.delete(id);
        } catch (RuntimeException ex) {
            log.warn("No se pudo eliminar la imagen {} al revertir el alta de la persona: {}", id, ex.getMessage());
        }
    }

    private void eliminarContactoTelefonicoSeguro(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        try {
            contactoTelefonicoRepository.delete(id);
        } catch (RuntimeException ignored) {
            // Puede no existir si se utilizó un contacto de correo
        }
    }

    private void eliminarContactoCorreoSeguro(String id) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        try {
            contactoCorreoRepository.delete(id);
        } catch (RuntimeException ignored) {
            // Puede no existir si se utilizó un contacto telefónico
        }
    }

    private void validarPersonaBase(PersonaDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe completar los datos de la persona.");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            throw new IllegalArgumentException("El nombre de la persona es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getApellido())) {
            throw new IllegalArgumentException("El apellido de la persona es obligatorio.");
        }
        if (dto.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        if (dto.getTipoDocumento() == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getNumeroDocumento())) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
    }

    private void validarPersonaCompleta(PersonaDto dto) {
        if (!StringUtils.hasText(dto.getContactoId())) {
            throw new IllegalArgumentException("Debe asociar un contacto a la persona.");
        }
        if (!StringUtils.hasText(dto.getDireccionId())) {
            throw new IllegalArgumentException("Debe asociar una dirección a la persona.");
        }
        if (!StringUtils.hasText(dto.getImagenId())) {
            throw new IllegalArgumentException("Debe asociar una imagen a la persona.");
        }
    }

    private void validarDireccionNueva(DireccionDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe completar los datos de la dirección.");
        }
        if (!StringUtils.hasText(dto.getCalle())) {
            throw new IllegalArgumentException("La calle de la dirección es obligatoria.");
        }
        if (!StringUtils.hasText(dto.getNumeracion())) {
            throw new IllegalArgumentException("La numeración de la dirección es obligatoria.");
        }
        if (!StringUtils.hasText(dto.getLocalidadId())) {
            throw new IllegalArgumentException("Debe seleccionar la localidad de la dirección.");
        }
    }

    private void validarImagenNueva(ImagenDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe proporcionar los datos de la imagen.");
        }
        if (dto.getContenido() == null || dto.getContenido().length == 0) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getNombre())) {
            dto.setNombre("Imagen persona");
        }
        if (!StringUtils.hasText(dto.getMime())) {
            dto.setMime("image/png");
        }
        if (dto.getTipoImagen() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de imagen.");
        }
    }

    private void validarContactoTelefonicoNuevo(ContactoTelefonicoDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getTelefono())) {
            throw new IllegalArgumentException("Debe ingresar un teléfono de contacto.");
        }
        if (dto.getTipoTelefono() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de teléfono.");
        }
        if (dto.getTipoContacto() == null) {
            throw new IllegalArgumentException("Debe indicar el tipo de contacto telefónico.");
        }
    }

    private void validarContactoCorreoNuevo(ContactoCorreoElectronicoDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("Debe ingresar un correo electrónico.");
        }
        if (dto.getTipoContacto() == null) {
            throw new IllegalArgumentException("Debe indicar el tipo de contacto de correo.");
        }
    }

    private boolean usaContactoCorreo(String tipoContactoPreferido) {
        return StringUtils.hasText(tipoContactoPreferido)
                && ("CORREO".equalsIgnoreCase(tipoContactoPreferido)
                || "EMAIL".equalsIgnoreCase(tipoContactoPreferido));
    }

    private boolean personaValida(PersonaDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombre());
    }
}
