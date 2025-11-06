package com.contactos.controller.view;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.enumeration.TipoTelefono;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.EmpresaService;
import com.contactos.controller.view.dto.ContactoCorreoForm;
import com.contactos.controller.view.dto.ContactoTelefonoForm;
import com.contactos.controller.view.dto.EmpresaForm;
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
@RequestMapping("/empresas")
public class EmpresaViewController {

    private final EmpresaService empresaService;

    public EmpresaViewController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @ModelAttribute("tiposTelefono")
    public TipoTelefono[] tiposTelefono() {
        return TipoTelefono.values();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public String listarEmpresas(Model model) {
        try {
            List<Empresa> empresas = empresaService.listarActivasConContactos().stream()
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
            Empresa empresa = empresaService.obtenerConContactos(id);
            model.addAttribute("empresa", empresa);
            List<Contacto> contactosActivos = empresa.getContactos().stream()
                    .filter(contacto -> !Boolean.TRUE.equals(contacto.isEliminado()))
                    .collect(Collectors.toList());
            model.addAttribute("contactosActivos", contactosActivos);
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
            empresaService.crearEmpresa(empresa);
            redirectAttributes.addFlashAttribute("exito", "La empresa se registró correctamente.");
            return "redirect:/empresas";
        } catch (IllegalArgumentException | ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("esEdicion", false);
            return "empresas/formulario";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editarEmpresa(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Empresa empresa = empresaService.obtenerConContactos(id);
            if (!model.containsAttribute("empresaForm")) {
                model.addAttribute("empresaForm", mapearFormulario(empresa));
            }
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
            Set<Long> contactosEliminar = obtenerContactosEliminar(form);
            empresaService.actualizarEmpresa(id, empresa, contactosEliminar);
            redirectAttributes.addFlashAttribute("exito", "La empresa se actualizó correctamente.");
            return "redirect:/empresas";
        } catch (IllegalArgumentException | ErrorServiceException e) {
            model.addAttribute("error", e.getMessage());
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

    private Empresa construirEmpresaDesdeForm(EmpresaForm form) {
        Empresa empresa = new Empresa();
        empresa.setId(form.getId());
        empresa.setNombre(StringUtils.trimWhitespace(form.getNombre()));
        empresa.setContactos(construirContactos(form));
        return empresa;
    }

    private Set<Contacto> construirContactos(EmpresaForm form) {
        Set<Contacto> contactos = new HashSet<>();

        if (form.getCorreos() != null) {
            for (ContactoCorreoForm correoForm : form.getCorreos()) {
                if (correoForm == null) {
                    continue;
                }
                String email = StringUtils.trimWhitespace(correoForm.getEmail());
                if (!StringUtils.hasText(email)) {
                    if (correoForm.getId() != null) {
                        throw new IllegalArgumentException("Para eliminar un correo usá el icono de basura.");
                    }
                    continue;
                }
                ContactoCorreoElectronico contacto = new ContactoCorreoElectronico();
                contacto.setId(correoForm.getId());
                contacto.setEmail(email);
                contacto.setEliminado(Boolean.FALSE);
                contactos.add(contacto);
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

                ContactoTelefonico contacto = new ContactoTelefonico();
                contacto.setId(telefonoForm.getId());
                contacto.setTelefono(numero);
                contacto.setTipoTelefono(telefonoForm.getTipo());
                contacto.setEliminado(Boolean.FALSE);
                contactos.add(contacto);
            }
        }

        return contactos;
    }

    private EmpresaForm mapearFormulario(Empresa empresa) {
        EmpresaForm form = new EmpresaForm();
        form.setId(empresa.getId());
        form.setNombre(empresa.getNombre());

        if (empresa.getContactos() != null) {
            empresa.getContactos().stream()
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
        }
        return form;
    }

    private Set<Long> obtenerContactosEliminar(EmpresaForm form) {
        if (form.getContactosEliminar() == null) {
            return new HashSet<>();
        }
        return form.getContactosEliminar().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
