package org.consultorio.demo.controller.view;

import org.consultorio.demo.bussiness.domain.TemplateEntity;
import org.consultorio.demo.bussiness.logic.service.TemplateService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

public abstract class TemplateController<T extends TemplateEntity> {

    protected abstract TemplateService<T> getService();
    
    protected abstract String getEntityName();
    
    protected abstract String getBaseUrl();

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", getService().listarActivos());
        return getBaseUrl() + "/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        return getBaseUrl() + "/formulario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<T> entityOpt = getService().buscarPorId(id);
        if (entityOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", getEntityName() + " no encontrado");
            return "redirect:" + getBaseUrl();
        }
        model.addAttribute("entity", entityOpt.get());
        return getBaseUrl() + "/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute T entity, RedirectAttributes redirectAttributes) {
        try {
            if (entity.getId() == null || entity.getId().isEmpty()) {
                getService().crear(entity);
                redirectAttributes.addFlashAttribute("exito", getEntityName() + " creado exitosamente");
            } else {
                getService().modificar(entity);
                redirectAttributes.addFlashAttribute("exito", getEntityName() + " modificado exitosamente");
            }
            return "redirect:" + getBaseUrl();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:" + getBaseUrl() + "/crear";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            getService().eliminar(id);
            redirectAttributes.addFlashAttribute("exito", getEntityName() + " eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:" + getBaseUrl();
    }
}
