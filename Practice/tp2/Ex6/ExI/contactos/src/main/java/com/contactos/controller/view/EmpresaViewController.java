package com.contactos.controller.view;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.domain.enumeration.TipoTelefono;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.EmpresaService;
import com.contactos.business.logic.service.PersonaService;
import com.contactos.controller.view.dto.EmpresaForm;
import com.contactos.controller.view.dto.TipoContactoEmpresaForm;
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
@RequestMapping("/empresas")
public class EmpresaViewController {

    private final EmpresaService empresaService;
    private final PersonaService personaService;

    public EmpresaViewController(EmpresaService empresaService,
                                 PersonaService personaService) {
        this.empresaService = empresaService;
        this.personaService = personaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public String listarEmpresas(Model model) {
        try {
            List<Empresa> empresas = empresaService.listarActivos().stream()
                    .sorted(Comparator.comparing(Empresa::getNombre, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            model.addAttribute("empresas", empresas);
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "empresas/lista";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Empresa empresa = empresaService.obtener(id)
                    .orElseThrow(() -> new ErrorServiceException("La empresa indicada no existe"));
            model.addAttribute("empresa", empresa);
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/empresas";
        }
        return "empresas/detalle";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nueva")
    public String nuevaEmpresa(Model model) {
        if (!model.containsAttribute("empresaForm")) {
            model.addAttribute("empresaForm", new EmpresaForm());
        }
        prepararFormulario(model);
        model.addAttribute("esEdicion", false);
        return "empresas/formulario";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String crearEmpresa(@ModelAttribute("empresaForm") EmpresaForm form,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Empresa empresa = construirEmpresaDesdeForm(form);
            empresaService.alta(empresa);
            redirectAttributes.addFlashAttribute("exito", "La empresa se registró correctamente.");
            return "redirect:/empresas";
        } catch (IllegalArgumentException | ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model);
            model.addAttribute("esEdicion", false);
            return "empresas/formulario";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editarEmpresa(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Empresa empresa = empresaService.obtener(id)
                    .orElseThrow(() -> new ErrorServiceException("La empresa indicada no existe"));
            if (!model.containsAttribute("empresaForm")) {
                model.addAttribute("empresaForm", mapearFormulario(empresa));
            }
            prepararFormulario(model);
            model.addAttribute("esEdicion", true);
            return "empresas/formulario";
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/empresas";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/actualizar")
    public String actualizarEmpresa(@PathVariable Long id,
                                    @ModelAttribute("empresaForm") EmpresaForm form,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        form.setId(id);
        try {
            Empresa empresa = construirEmpresaDesdeForm(form);
            empresaService.modificar(id, empresa)
                    .orElseThrow(() -> new ErrorServiceException("La empresa indicada no existe"));
            redirectAttributes.addFlashAttribute("exito", "La empresa se actualizó correctamente.");
            return "redirect:/empresas";
        } catch (IllegalArgumentException | ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model);
            model.addAttribute("esEdicion", true);
            return "empresas/formulario";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/eliminar")
    public String eliminarEmpresa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            empresaService.baja(id);
            redirectAttributes.addFlashAttribute("exito", "La empresa se eliminó correctamente.");
        } catch (ErrorServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/empresas";
    }

    private void prepararFormulario(Model model) {
        try {
            List<Persona> personas = personaService.listarActivasConRelaciones().stream()
                    .sorted(Comparator.comparing(Persona::getNombre, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(Persona::getApellido, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            model.addAttribute("personas", personas);
        } catch (ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("tiposTelefono", TipoTelefono.values());
    }

    private Empresa construirEmpresaDesdeForm(EmpresaForm form) {
        validarDatosDeContacto(form);

        Empresa empresa = new Empresa();
        empresa.setId(form.getId());
        empresa.setNombre(StringUtils.trimWhitespace(form.getNombre()));

        Persona persona = new Persona();
        persona.setId(form.getPersonaId());
        empresa.setPersona(persona);

        Contacto contacto = crearContactoDesdeForm(form);
        empresa.setContacto(contacto);
        return empresa;
    }

    private void validarDatosDeContacto(EmpresaForm form) {
        if (form.getTipoContacto() == TipoContactoEmpresaForm.TELEFONO) {
            if (!StringUtils.hasText(form.getTelefono())) {
                throw new IllegalArgumentException("Debe indicar un teléfono laboral.");
            }
            if (form.getTipoTelefono() == null) {
                throw new IllegalArgumentException("Debe seleccionar el tipo de teléfono.");
            }
        } else if (!StringUtils.hasText(form.getCorreo())) {
            throw new IllegalArgumentException("Debe indicar un correo laboral.");
        }
    }

    private Contacto crearContactoDesdeForm(EmpresaForm form) {
        if (form.getTipoContacto() == TipoContactoEmpresaForm.TELEFONO) {
            ContactoTelefonico contacto = new ContactoTelefonico();
            contacto.setId(form.getContactoId());
            contacto.setTelefono(StringUtils.trimWhitespace(form.getTelefono()));
            contacto.setTipoTelefono(form.getTipoTelefono());
            contacto.setObservacion(StringUtils.trimWhitespace(form.getObservacion()));
            contacto.setEliminado(Boolean.FALSE);
            return contacto;
        }
        ContactoCorreoElectronico contacto = new ContactoCorreoElectronico();
        contacto.setId(form.getContactoId());
        contacto.setEmail(StringUtils.trimWhitespace(form.getCorreo()));
        contacto.setObservacion(StringUtils.trimWhitespace(form.getObservacion()));
        contacto.setEliminado(Boolean.FALSE);
        return contacto;
    }

    private EmpresaForm mapearFormulario(Empresa empresa) {
        EmpresaForm form = new EmpresaForm();
        form.setId(empresa.getId());
        form.setNombre(empresa.getNombre());
        if (empresa.getPersona() != null) {
            form.setPersonaId(empresa.getPersona().getId());
        }
        Contacto contacto = empresa.getContacto();
        if (contacto != null) {
            form.setContactoId(contacto.getId());
            form.setObservacion(contacto.getObservacion());
            if (contacto instanceof ContactoTelefonico telefonico) {
                form.setTipoContacto(TipoContactoEmpresaForm.TELEFONO);
                form.setTelefono(telefonico.getTelefono());
                form.setTipoTelefono(telefonico.getTipoTelefono());
            } else if (contacto instanceof ContactoCorreoElectronico correo) {
                form.setTipoContacto(TipoContactoEmpresaForm.CORREO);
                form.setCorreo(correo.getEmail());
            }
        }
        return form;
    }
}
