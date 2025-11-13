package com.car.clientead.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import com.car.clientead.business.logic.LocalidadService;
import com.car.clientead.business.logic.PersonaService;
import com.car.clientead.business.logic.UsuarioService;
import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.LocalidadDto;
import com.car.clientead.client.dto.PersonaDto;
import com.car.clientead.client.dto.UsuarioDto;
import com.car.clientead.client.dto.enums.RolUsuario;
import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.dto.enums.TipoDocumento;
import com.car.clientead.client.dto.enums.TipoImagen;
import com.car.clientead.client.dto.enums.TipoTelefono;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final String REDIRECT_USUARIOS = "redirect:/usuarios";
    private static final String LIST_VIEW = "lUsuario.html";
    private static final String FORM_VIEW = "eUsuario.html";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private LocalidadService localidadService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", usuarioService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        cargarPersonas(model);
        model.addAttribute("titleList", "Listado de Usuarios");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String mostrarAlta(Model model) {
        prepararFormulario(model, new UsuarioDto(), new PersonaDto(), "Alta de Usuario", false);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute UsuarioDto dto,
                        @ModelAttribute("personaForm") PersonaDto personaForm,
                        @ModelAttribute("nuevaDireccion") DireccionDto nuevaDireccion,
                        @ModelAttribute("nuevaImagen") ImagenDto nuevaImagen,
                        @ModelAttribute("nuevoContactoTelefonico") ContactoTelefonicoDto nuevoContactoTelefonico,
                        @ModelAttribute("nuevoContactoCorreo") ContactoCorreoElectronicoDto nuevoContactoCorreo,
                        @RequestParam(name = "tipoContactoNuevo", required = false) String tipoContactoNuevo,
                        @RequestParam(name = "personaImagenArchivo", required = false) MultipartFile personaImagenArchivo,
                        Model model) {
        PersonaDto personaCreada = null;
        String contactoPreferido = StringUtils.hasText(tipoContactoNuevo) ? tipoContactoNuevo : "TELEFONO";
        try {
            prepararContenidoImagen(nuevaImagen, personaImagenArchivo, true);
            personaCreada = personaService.crearConDatosRelacionados(
                    personaForm,
                    nuevaDireccion,
                    nuevaImagen,
                    nuevoContactoTelefonico,
                    nuevoContactoCorreo,
                    contactoPreferido
            );
            dto.setPersonaId(personaCreada.getId());
            usuarioService.crear(dto);
            return REDIRECT_USUARIOS;
        } catch (Exception ex) {
            if (personaCreada != null) {
                personaService.eliminarConRelaciones(personaCreada.getId());
            }
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("tipoContactoNuevo", contactoPreferido);
            model.addAttribute("item", dto);
            model.addAttribute("personaForm", personaForm);
            model.addAttribute("nuevaDireccion", nuevaDireccion);
            model.addAttribute("nuevaImagen", nuevaImagen);
            model.addAttribute("nuevoContactoTelefonico", nuevoContactoTelefonico);
            model.addAttribute("nuevoContactoCorreo", nuevoContactoCorreo);
            prepararFormulario(model, dto, personaForm, "Alta de Usuario", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            UsuarioDto usuario = usuarioService.consultar(id);
            PersonaDto persona = obtenerPersonaUsuario(usuario);
            prepararFormulario(model, usuario, persona, "Detalle de Usuario", true);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_USUARIOS;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            UsuarioDto usuario = usuarioService.consultar(id);
            PersonaDto persona = obtenerPersonaUsuario(usuario);
            prepararFormulario(model, usuario, persona, "Modificar Usuario", false);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_USUARIOS;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, @ModelAttribute UsuarioDto dto, Model model) {
        try {
            usuarioService.modificar(id, dto);
            return REDIRECT_USUARIOS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            PersonaDto persona = obtenerPersonaUsuario(dto);
            prepararFormulario(model, dto, persona, "Modificar Usuario", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            usuarioService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar usuario: " + ex.getMessage());
        }
        return REDIRECT_USUARIOS;
    }

    private void prepararFormulario(Model model,
                                    UsuarioDto usuario,
                                    PersonaDto persona,
                                    String titulo,
                                    boolean modoVer) {
        model.addAttribute("item", usuario);
        model.addAttribute("titleForm", titulo);
        model.addAttribute("modoVer", modoVer);
        cargarRoles(model);
        boolean esAlta = !StringUtils.hasText(usuario.getId());
        model.addAttribute("esAltaUsuario", esAlta);
        if (!model.containsAttribute("personaForm")) {
            model.addAttribute("personaForm", persona != null ? persona : new PersonaDto());
        }
        model.addAttribute("personaDetalle", persona);
        model.addAttribute("mostrarFormularioPersona", esAlta && !modoVer);
        if (esAlta && !model.containsAttribute("tipoContactoNuevo")) {
            model.addAttribute("tipoContactoNuevo", "TELEFONO");
        }
        if (esAlta) {
            ensureAttribute(model, "nuevoContactoTelefonico", this::crearContactoTelefonicoPorDefecto);
            ensureAttribute(model, "nuevoContactoCorreo", this::crearContactoCorreoPorDefecto);
            ensureAttribute(model, "nuevaDireccion", DireccionDto::new);
            ensureAttribute(model, "nuevaImagen", () -> {
                ImagenDto imagen = new ImagenDto();
                imagen.setTipoImagen(TipoImagen.PERSONA);
                return imagen;
            });
        }
        cargarCatalogosPersona(model);
    }

    private PersonaDto obtenerPersonaUsuario(UsuarioDto usuario) {
        if (usuario == null || !StringUtils.hasText(usuario.getPersonaId())) {
            return null;
        }
        try {
            return personaService.consultar(usuario.getPersonaId());
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private void cargarCatalogosPersona(Model model) {
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("tiposTelefono", TipoTelefono.values());
        model.addAttribute("tiposContacto", TipoContacto.values());
        model.addAttribute("tiposImagen", TipoImagen.values());
        cargarLocalidades(model);
    }

    private void cargarLocalidades(Model model) {
        try {
            List<LocalidadDto> localidades = localidadService.listar();
            model.addAttribute("localidades", localidades);
        } catch (ApiClientException ex) {
            model.addAttribute("localidades", Collections.<LocalidadDto>emptyList());
            appendError(model, ex.getMessage());
        }
    }

    private void cargarPersonas(Model model) {
        try {
            List<PersonaDto> personas = personaService.listar();
            model.addAttribute("personas", personas);
            Map<String, String> nombres = personas.stream()
                    .filter(Objects::nonNull)
                    .filter(persona -> persona.getId() != null)
                    .collect(Collectors.toMap(
                            PersonaDto::getId,
                            this::formatearPersona,
                            (a, b) -> a));
            model.addAttribute("personaNombrePorId", nombres);
        } catch (ApiClientException ex) {
            model.addAttribute("personas", Collections.<PersonaDto>emptyList());
            model.addAttribute("personaNombrePorId", Collections.emptyMap());
            appendError(model, ex.getMessage());
        }
    }

    private void cargarRoles(Model model) {
        model.addAttribute("roles", RolUsuario.values());
    }

    private String formatearPersona(PersonaDto persona) {
        StringBuilder sb = new StringBuilder();
        if (persona != null) {
            if (persona.getNombre() != null) {
                sb.append(persona.getNombre());
            }
            if (persona.getApellido() != null) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(persona.getApellido());
            }
            if (persona.getNumeroDocumento() != null) {
                sb.append(" (").append(persona.getNumeroDocumento()).append(")");
            }
        }
        return sb.length() > 0 ? sb.toString() : "Persona sin datos";
    }

    private ContactoTelefonicoDto crearContactoTelefonicoPorDefecto() {
        ContactoTelefonicoDto dto = new ContactoTelefonicoDto();
        dto.setTipoTelefono(TipoTelefono.CELULAR);
        dto.setTipoContacto(TipoContacto.PERSONAL);
        return dto;
    }

    private ContactoCorreoElectronicoDto crearContactoCorreoPorDefecto() {
        ContactoCorreoElectronicoDto dto = new ContactoCorreoElectronicoDto();
        dto.setTipoContacto(TipoContacto.PERSONAL);
        return dto;
    }

    private void prepararContenidoImagen(ImagenDto imagen, MultipartFile archivo, boolean requerida) {
        if (imagen == null) {
            throw new IllegalArgumentException("Debe completar los datos de la imagen.");
        }
        if (archivo == null || archivo.isEmpty()) {
            if (requerida) {
                throw new IllegalArgumentException("Debe adjuntar la imagen de la persona asociada.");
            }
            return;
        }
        try {
            imagen.setContenido(archivo.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo procesar el archivo de imagen.");
        }
        imagen.setMime(archivo.getContentType());
        if (!StringUtils.hasText(imagen.getNombre())) {
            imagen.setNombre(archivo.getOriginalFilename());
        }
        if (imagen.getTipoImagen() == null) {
            imagen.setTipoImagen(TipoImagen.PERSONA);
        }
    }

    private <T> void ensureAttribute(Model model, String attributeName, Supplier<T> supplier) {
        if (!model.containsAttribute(attributeName)) {
            model.addAttribute(attributeName, supplier.get());
        }
    }

    private void appendError(Model model, String newMessage) {
        Object existing = model.asMap().get("errorMessage");
        String message = existing != null
                ? existing.toString() + " " + newMessage
                : newMessage;
        model.addAttribute("errorMessage", message);
    }
}
