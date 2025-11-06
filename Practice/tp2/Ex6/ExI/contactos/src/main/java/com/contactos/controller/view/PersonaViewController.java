package com.contactos.controller.view;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.domain.Usuario;
import com.contactos.business.domain.enumeration.TipoContacto;
import com.contactos.business.domain.enumeration.TipoTelefono;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.EmpresaService;
import com.contactos.business.logic.service.PersonaGestionService;
import com.contactos.business.logic.service.PersonaService;
import com.contactos.controller.view.dto.ContactoCorreoForm;
import com.contactos.controller.view.dto.ContactoTelefonoForm;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/personas")
public class PersonaViewController {

    private final PersonaService personaService;
    private final PersonaGestionService personaGestionService;
    private final EmpresaService empresaService;

    public PersonaViewController(PersonaService personaService,
                                 PersonaGestionService personaGestionService,
                                 EmpresaService empresaService) {
        this.personaService = personaService;
        this.personaGestionService = personaGestionService;
        this.empresaService = empresaService;
    }

    @ModelAttribute("tiposTelefono")
    public TipoTelefono[] tiposTelefono() {
        return TipoTelefono.values();
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
        agregarEmpresasDisponibles(model, null);
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
            persona.setContactos(construirContactos(form));

            Usuario usuario = new Usuario();
            usuario.setCuenta(StringUtils.trimWhitespace(form.getCuenta()));
            usuario.setClave(form.getClave());

            Set<Long> empresasSeleccionadas = obtenerEmpresasSeleccionadas(form);

            personaGestionService.crearPersonaConUsuario(persona, usuario, empresasSeleccionadas);
            redirectAttributes.addFlashAttribute("exito", "La persona se registró correctamente.");
            return "redirect:/personas";
        } catch (IllegalArgumentException | ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("esEdicion", false);
            agregarEmpresasDisponibles(model, null);
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
            agregarEmpresasDisponibles(model, persona.getId());
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
            persona.setContactos(construirContactos(form));

            Usuario usuario = new Usuario();
            usuario.setId(form.getUsuarioId());
            usuario.setCuenta(StringUtils.trimWhitespace(form.getCuenta()));
            usuario.setClave(StringUtils.hasText(form.getClave()) ? form.getClave() : null);

            Set<Long> contactosEliminar = obtenerContactosEliminar(form);
            Set<Long> empresasSeleccionadas = obtenerEmpresasSeleccionadas(form);

            personaGestionService.actualizarPersonaConUsuario(persona, usuario, contactosEliminar, empresasSeleccionadas);
            redirectAttributes.addFlashAttribute("exito", "La persona se actualizó correctamente.");
            return "redirect:/personas";
        } catch (IllegalArgumentException | ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("esEdicion", true);
            agregarEmpresasDisponibles(model, id);
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
        persona.getContactos().stream()
                .filter(contacto -> !Boolean.TRUE.equals(contacto.isEliminado()))
                .forEach(contacto -> {
                    if (contacto instanceof ContactoCorreoElectronico correo) {
                        ContactoCorreoForm correoForm = new ContactoCorreoForm();
                        correoForm.setId(correo.getId());
                        correoForm.setEmail(correo.getEmail());
                        form.getCorreos().add(correoForm);
                    } else if (contacto instanceof ContactoTelefonico telefono) {
                        ContactoTelefonoForm telefonoForm = new ContactoTelefonoForm();
                        telefonoForm.setId(telefono.getId());
                        telefonoForm.setNumero(telefono.getTelefono());
                        telefonoForm.setTipo(telefono.getTipoTelefono());
                        form.getTelefonos().add(telefonoForm);
                    }
                });
        persona.getEmpresas().stream()
                .filter(empresa -> !Boolean.TRUE.equals(empresa.isEliminado()))
                .forEach(empresa -> form.getEmpresasIds().add(empresa.getId()));
        return form;
    }

    private Set<Contacto> construirContactos(PersonaForm form) {
        Set<Contacto> contactos = new HashSet<>();

        if (form.getCorreos() != null) {
            for (ContactoCorreoForm correoForm : form.getCorreos()) {
                if (correoForm == null) {
                    continue;
                }
                String email = StringUtils.trimWhitespace(correoForm.getEmail());
                if (!StringUtils.hasText(email)) {
                    if (correoForm.getId() != null) {
                        throw new IllegalArgumentException("Para eliminar un correo electrónico usá el ícono de basura.");
                    }
                    continue;
                }
                ContactoCorreoElectronico correo = new ContactoCorreoElectronico();
                correo.setId(correoForm.getId());
                correo.setEmail(email);
                correo.setTipoContacto(TipoContacto.PERSONAL);
                correo.setEliminado(Boolean.FALSE);
                contactos.add(correo);
            }
        }

        if (form.getTelefonos() != null) {
            for (ContactoTelefonoForm telefonoForm : form.getTelefonos()) {
                if (telefonoForm == null) {
                    continue;
                }
                String numero = StringUtils.trimWhitespace(telefonoForm.getNumero());
                boolean tieneNumero = StringUtils.hasText(numero);
                boolean tieneTipo = telefonoForm.getTipo() != null;

                if (!tieneNumero && !tieneTipo) {
                    continue;
                }
                if (!tieneNumero) {
                    throw new IllegalArgumentException("Debe indicar un número de teléfono.");
                }
                if (!tieneTipo) {
                    throw new IllegalArgumentException("Debe seleccionar el tipo de teléfono.");
                }

                ContactoTelefonico telefono = new ContactoTelefonico();
                telefono.setId(telefonoForm.getId());
                telefono.setTelefono(numero);
                telefono.setTipoTelefono(telefonoForm.getTipo());
                telefono.setTipoContacto(TipoContacto.PERSONAL);
                telefono.setEliminado(Boolean.FALSE);
                contactos.add(telefono);
            }
        }

        return contactos;
    }

    private Set<Long> obtenerContactosEliminar(PersonaForm form) {
        if (form.getContactosEliminar() == null) {
            return new HashSet<>();
        }
        return form.getContactosEliminar().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Long> obtenerEmpresasSeleccionadas(PersonaForm form) {
        if (form.getEmpresasIds() == null) {
            return new HashSet<>();
        }
        return form.getEmpresasIds().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void agregarEmpresasDisponibles(Model model, Long personaId) {
        model.addAttribute("empresasDisponibles", List.of());
        try {
            List<Empresa> empresas = empresaService.listarActivasConContactos().stream()
                    .filter(empresa -> !Boolean.TRUE.equals(empresa.isEliminado()))
                    .filter(empresa -> empresa.getPersona() == null ||
                            (personaId != null && empresa.getPersona() != null && personaId.equals(empresa.getPersona().getId())))
                    .sorted(Comparator.comparing(Empresa::getNombre, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            model.addAttribute("empresasDisponibles", empresas);
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
        }
    }
}
