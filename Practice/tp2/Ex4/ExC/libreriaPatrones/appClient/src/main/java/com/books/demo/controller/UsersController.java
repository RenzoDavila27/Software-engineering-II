package com.books.demo.controller;

import com.books.demo.bussiness.logic.DomicilioService;
import com.books.demo.bussiness.logic.LibroService;
import com.books.demo.bussiness.logic.LocalidadService;
import com.books.demo.bussiness.logic.PersonaService;
import com.books.demo.client.dto.DomicilioDto;
import com.books.demo.client.dto.LibroDto;
import com.books.demo.client.dto.LocalidadDto;
import com.books.demo.client.dto.PersonaDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.controller.view.PersonaForm;
import java.util.List;
import java.util.Optional;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsersController {

    private static final String REDIRECT_USUARIOS = "redirect:/usuarios";

    private final PersonaService personaService;
    private final DomicilioService domicilioService;
    private final LocalidadService localidadService;
    private final LibroService libroService;

    public UsersController(PersonaService personaService,
                           DomicilioService domicilioService,
                           LocalidadService localidadService,
                           LibroService libroService) {
        this.personaService = personaService;
        this.domicilioService = domicilioService;
        this.localidadService = localidadService;
        this.libroService = libroService;
    }

    @GetMapping
    public String users(Model model) {
        try {
            model.addAttribute("personas", personaService.listarPersonas());
        } catch (ApiClientException ex) {
            model.addAttribute("personas", java.util.Collections.emptyList());
            Object existing = model.asMap().get("errorMessage");
            String message = existing != null
                    ? existing.toString() + " " + ex.getMessage()
                    : ex.getMessage();
            model.addAttribute("errorMessage", message);
        }
        return "users";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        prepararFormularioNuevo(model);
        return "users-form";
    }

    @PostMapping
    public String crearUsuario(@ModelAttribute PersonaForm personaForm,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            PersonaDto personaDto = construirPersonaDesdeForm(null, personaForm);

            Long domicilioId = null;
            if (tieneDatosDomicilio(personaForm)) {
                DomicilioDto domicilioDto = construirDomicilioDesdeForm(personaForm);
                domicilioId = domicilioService.crearDomicilio(domicilioDto).getId();
            }
            personaDto.setDomicilioId(domicilioId);

            personaService.crearPersona(personaDto);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado correctamente.");
            return REDIRECT_USUARIOS;
        } catch (IllegalArgumentException | ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormularioNuevo(model);
            return "users-form";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Ocurrio un error inesperado al crear el usuario.");
            prepararFormularioNuevo(model);
            return "users-form";
        }
    }

    @GetMapping("/{id}")
    public String detalleUsuario(@PathVariable Long id,
                                 @RequestParam(name = "mode", defaultValue = "consultar") String mode,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            return personaService.obtenerPersona(id)
                    .map(persona -> prepararVistaDetalle(persona, mode, model))
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "El usuario solicitado no existe." );
                        return REDIRECT_USUARIOS;
                    });
        } catch (ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return REDIRECT_USUARIOS;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al cargar el usuario.");
            return REDIRECT_USUARIOS;
        }
    }

    @PostMapping("/{id}/modificar")
    public String modificarUsuario(@PathVariable Long id,
                                   @ModelAttribute PersonaForm personaForm,
                                   RedirectAttributes redirectAttributes) {
        try {
            PersonaDto personaDto = construirPersonaDesdeForm(id, personaForm);
            Long domicilioId = personaForm.getDomicilioId();
            DomicilioDto domicilioDto = construirDomicilioDesdeForm(personaForm);

            if (domicilioId != null) {
                domicilioService.actualizarDomicilio(domicilioId, domicilioDto);
                personaDto.setDomicilioId(domicilioId);
            } else if (tieneDatosDomicilio(personaForm)) {
                DomicilioDto creado = domicilioService.crearDomicilio(domicilioDto);
                personaDto.setDomicilioId(creado.getId());
            }

            personaService.actualizarPersona(id, personaDto);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente.");
            return REDIRECT_USUARIOS;
        } catch (IllegalArgumentException | ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/usuarios/" + id + "?mode=modificar";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al actualizar el usuario.");
            return "redirect:/usuarios/" + id + "?mode=modificar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<PersonaDto> personaOpt = personaService.obtenerPersona(id);
            if (personaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El usuario solicitado no existe.");
                return REDIRECT_USUARIOS;
            }

            PersonaDto persona = personaOpt.get();
            Long domicilioId = persona.getDomicilioId();
            List<LibroDto> librosAsignados = libroService.listarLibrosPorPersona(id);

            libroService.desasignarLibrosDePersona(id);
            personaService.eliminarPersona(id);
            if (domicilioId != null) {
                domicilioService.eliminarDomicilio(domicilioId);
            }
            StringBuilder success = new StringBuilder("Usuario eliminado correctamente.");
            if (domicilioId != null) {
                success.append(" Domicilio eliminado.");
            }
            if (!librosAsignados.isEmpty()) {
                success.append(" ").append(librosAsignados.size()).append(" libro(s) quedaron sin socio asignado.");
            }
            redirectAttributes.addFlashAttribute("successMessage", success.toString());
            return REDIRECT_USUARIOS;
        } catch (IllegalArgumentException | ApiClientException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/usuarios/" + id + "?mode=eliminar";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrio un error inesperado al eliminar el usuario.");
            return "redirect:/usuarios/" + id + "?mode=eliminar";
        }
    }

    private String normalizarModo(String mode) {
        if (mode == null) {
            return "consultar";
        }
        return switch (mode.toLowerCase()) {
            case "modificar", "editar" -> "modificar";
            case "eliminar", "borrar" -> "eliminar";
            default -> "consultar";
        };
    }

    private String tituloPorModo(String entidad, String mode) {
        return switch (mode) {
            case "modificar" -> "Modificar " + entidad;
            case "eliminar" -> "Eliminar " + entidad;
            default -> "Consultar " + entidad;
        };
    }

    private String prepararVistaDetalle(PersonaDto persona,
                                        String mode,
                                        Model model) {
        String normalizedMode = normalizarModo(mode);
        model.addAttribute("persona", persona);
        model.addAttribute("mode", normalizedMode);
        model.addAttribute("pageTitle", tituloPorModo("Usuario", normalizedMode));

        DomicilioDto domicilio = null;
        if (persona.getDomicilioId() != null) {
            try {
                domicilio = domicilioService.obtenerDomicilio(persona.getDomicilioId()).orElse(null);
            } catch (ApiClientException ex) {
                acumularMensaje(model, ex.getMessage());
            }
        }
        model.addAttribute("domicilio", domicilio);

        LocalidadDto localidad = null;
        if (domicilio != null && domicilio.getLocalidadId() != null) {
            try {
                localidad = localidadService.obtenerLocalidad(domicilio.getLocalidadId()).orElse(null);
            } catch (ApiClientException ex) {
                acumularMensaje(model, ex.getMessage());
            }
        }
        model.addAttribute("localidad", localidad);

        List<LibroDto> librosAsignados = libroService.listarLibrosPorPersona(persona.getId());
        model.addAttribute("librosAsignados", librosAsignados);

        if ("modificar".equals(normalizedMode)) {
            PersonaForm form = construirForm(persona, domicilio);
            model.addAttribute("personaForm", form);
            try {
                List<LocalidadDto> localidades = localidadService.listarLocalidades();
                model.addAttribute("localidades", localidades);
            } catch (ApiClientException ex) {
                model.addAttribute("localidades", List.of());
                acumularMensaje(model, ex.getMessage());
            }
        }

        return "users-detail";
    }

    private PersonaForm construirForm(PersonaDto persona, DomicilioDto domicilio) {
        PersonaForm form = new PersonaForm();
        form.setId(persona.getId());
        form.setNombre(persona.getNombre());
        form.setApellido(persona.getApellido());
        form.setDni(persona.getDni());
        if (domicilio != null) {
            form.setDomicilioId(domicilio.getId());
            form.setCalle(domicilio.getCalle());
            form.setNumero(domicilio.getNumero());
            form.setLocalidadId(domicilio.getLocalidadId());
        }
        return form;
    }

    private PersonaDto construirPersonaDesdeForm(Long id, PersonaForm form) {
        PersonaDto dto = new PersonaDto();
        dto.setId(id);
        dto.setNombre(form.getNombre());
        dto.setApellido(form.getApellido());
        dto.setDni(form.getDni());
        return dto;
    }

    private DomicilioDto construirDomicilioDesdeForm(PersonaForm form) {
        DomicilioDto dto = new DomicilioDto();
        dto.setId(form.getDomicilioId());
        dto.setCalle(form.getCalle());
        dto.setNumero(form.getNumero());
        dto.setLocalidadId(form.getLocalidadId());
        return dto;
    }

    private void acumularMensaje(Model model, String nuevoMensaje) {
        Object existing = model.asMap().get("errorMessage");
        String message = existing != null
                ? existing.toString() + " " + nuevoMensaje
                : nuevoMensaje;
        model.addAttribute("errorMessage", message);
    }

    private boolean tieneDatosDomicilio(PersonaForm form) {
        return StringUtils.hasText(form.getCalle())
                || form.getNumero() != null
                || form.getLocalidadId() != null;
    }

    private void prepararFormularioNuevo(Model model) {
        if (!model.containsAttribute("personaForm")) {
            model.addAttribute("personaForm", new PersonaForm());
        }
        try {
            List<LocalidadDto> localidades = localidadService.listarLocalidades();
            model.addAttribute("localidades", localidades);
        } catch (ApiClientException ex) {
            model.addAttribute("localidades", List.of());
            acumularMensaje(model, ex.getMessage());
        }
    }
}
