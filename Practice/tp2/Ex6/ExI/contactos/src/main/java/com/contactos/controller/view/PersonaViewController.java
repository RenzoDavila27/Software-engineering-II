package com.contactos.controller.view;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.domain.Usuario;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.PersonaGestionService;
import com.contactos.business.logic.service.PersonaService;
import com.contactos.controller.view.dto.PersonaForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/personas")
public class PersonaViewController {

    private final PersonaService personaService;
    private final PersonaGestionService personaGestionService;

    public PersonaViewController(PersonaService personaService,
                                 PersonaGestionService personaGestionService) {
        this.personaService = personaService;
        this.personaGestionService = personaGestionService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public String listarPersonas(Model model) {
        try {
            List<Persona> personas = personaService.listarActivasConRelaciones().stream()
                    .sorted(Comparator.comparing(Persona::getApellido, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(Persona::getNombre, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            model.addAttribute("personas", personas);
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "personas/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Persona persona = personaService.obtenerConRelaciones(id)
                    .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe"));

            List<Usuario> cuentasActivas = persona.getUsuarios().stream()
                    .filter(usuario -> !Boolean.TRUE.equals(usuario.isEliminado()))
                    .collect(Collectors.toList());

            List<Empresa> empresasActivas = persona.getEmpresas().stream()
                    .filter(empresa -> !Boolean.TRUE.equals(empresa.isEliminado()))
                    .collect(Collectors.toList());

            List<Contacto> contactosActivos = persona.getContactos().stream()
                    .filter(contacto -> !Boolean.TRUE.equals(contacto.isEliminado()))
                    .collect(Collectors.toList());

            model.addAttribute("persona", persona);
            model.addAttribute("cuentasActivas", cuentasActivas);
            model.addAttribute("empresasActivas", empresasActivas);
            model.addAttribute("contactosActivos", contactosActivos);
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/personas";
        }
        return "personas/detalle";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nueva")
    public String nuevaPersona(Model model) {
        if (!model.containsAttribute("personaForm")) {
            model.addAttribute("personaForm", new PersonaForm());
        }
        model.addAttribute("esEdicion", false);
        return "personas/formulario";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String crearPersona(@ModelAttribute("personaForm") PersonaForm form,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Persona persona = new Persona();
            persona.setNombre(StringUtils.trimWhitespace(form.getNombre()));
            persona.setApellido(StringUtils.trimWhitespace(form.getApellido()));

            Usuario usuario = new Usuario();
            usuario.setCuenta(StringUtils.trimWhitespace(form.getCuenta()));
            usuario.setClave(form.getClave());

            personaGestionService.crearPersonaConUsuario(persona, usuario);
            redirectAttributes.addFlashAttribute("exito", "La persona se registró correctamente.");
            return "redirect:/personas";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("esEdicion", false);
            return "personas/formulario";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editarPersona(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Persona persona = personaService.obtenerConRelaciones(id)
                    .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe"));
            if (!model.containsAttribute("personaForm")) {
                model.addAttribute("personaForm", mapearFormulario(persona));
            }
            model.addAttribute("esEdicion", true);
            return "personas/formulario";
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/personas";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/actualizar")
    public String actualizarPersona(@PathVariable Long id,
                                    @ModelAttribute("personaForm") PersonaForm form,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        form.setId(id);
        try {
            Persona persona = new Persona();
            persona.setId(id);
            persona.setNombre(StringUtils.trimWhitespace(form.getNombre()));
            persona.setApellido(StringUtils.trimWhitespace(form.getApellido()));

            Usuario usuario = new Usuario();
            usuario.setId(form.getUsuarioId());
            usuario.setCuenta(StringUtils.trimWhitespace(form.getCuenta()));
            usuario.setClave(StringUtils.hasText(form.getClave()) ? form.getClave() : null);

            personaGestionService.actualizarPersonaConUsuario(persona, usuario);
            redirectAttributes.addFlashAttribute("exito", "La persona se actualizó correctamente.");
            return "redirect:/personas";
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("esEdicion", true);
            return "personas/formulario";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/eliminar")
    public String eliminarPersona(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            personaGestionService.eliminarPersonaConUsuarios(id);
            redirectAttributes.addFlashAttribute("exito", "La persona se eliminó correctamente.");
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/personas";
    }

    private PersonaForm mapearFormulario(Persona persona) {
        PersonaForm form = new PersonaForm();
        form.setId(persona.getId());
        form.setNombre(persona.getNombre());
        form.setApellido(persona.getApellido());

        persona.getUsuarios().stream()
                .filter(usuario -> !Boolean.TRUE.equals(usuario.isEliminado()))
                .findFirst()
                .ifPresent(usuario -> {
                    form.setUsuarioId(usuario.getId());
                    form.setCuenta(usuario.getCuenta());
                });
        return form;
    }
}
