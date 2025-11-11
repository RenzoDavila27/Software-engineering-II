package com.car.clientead.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.car.clientead.business.logic.PersonaService;
import com.car.clientead.business.logic.UsuarioService;
import com.car.clientead.client.dto.PersonaDto;
import com.car.clientead.client.dto.UsuarioDto;
import com.car.clientead.client.dto.enums.RolUsuario;
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
        model.addAttribute("item", new UsuarioDto());
        model.addAttribute("titleForm", "Alta de Usuario");
        model.addAttribute("modoVer", false);
        cargarPersonas(model);
        cargarRoles(model);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute UsuarioDto dto, Model model) {
        try {
            usuarioService.crear(dto);
            return REDIRECT_USUARIOS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Alta de Usuario");
            model.addAttribute("modoVer", false);
            cargarPersonas(model);
            cargarRoles(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", usuarioService.consultar(id));
            model.addAttribute("titleForm", "Detalle de Usuario");
            model.addAttribute("modoVer", true);
            cargarPersonas(model);
            cargarRoles(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_USUARIOS;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", usuarioService.consultar(id));
            model.addAttribute("titleForm", "Modificar Usuario");
            model.addAttribute("modoVer", false);
            cargarPersonas(model);
            cargarRoles(model);
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
            model.addAttribute("titleForm", "Modificar Usuario");
            model.addAttribute("modoVer", false);
            cargarPersonas(model);
            cargarRoles(model);
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

    private void appendError(Model model, String newMessage) {
        Object existing = model.asMap().get("errorMessage");
        String message = existing != null
                ? existing.toString() + " " + newMessage
                : newMessage;
        model.addAttribute("errorMessage", message);
    }
}
