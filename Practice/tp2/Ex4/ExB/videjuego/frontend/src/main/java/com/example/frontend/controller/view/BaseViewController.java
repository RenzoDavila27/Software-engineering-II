package com.example.frontend.controller.view;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.frontend.business.logic.error.ErrorServiceException;

import jakarta.validation.Valid;

public abstract class BaseViewController<DTO, FORM, ID> {

    private final String listView;
    private final String formView;
    private final String redirectList;
    private final String titleList;
    private final String titleForm;

    protected BaseViewController(String listView,
                                 String formView,
                                 String redirectList,
                                 String titleList,
                                 String titleForm) {
        this.listView = listView;
        this.formView = formView;
        this.redirectList = redirectList;
        this.titleList = titleList;
        this.titleForm = titleForm;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            List<DTO> items = obtenerListado();
            model.addAttribute("items", items);
            model.addAttribute("titleList", titleList);
            postListar(model, items);
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }
        return listView;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            model.addAttribute("form", crearFormularioVacio());
            model.addAttribute("isDisabled", false);
            model.addAttribute("titleForm", titleForm);
            cargarDatosFormulario(model);
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }
        return formView;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable ID id, Model model) {
        try {
            DTO dto = obtenerPorId(id);
            model.addAttribute("form", convertirAFormulario(dto));
            model.addAttribute("isDisabled", false);
            model.addAttribute("titleForm", titleForm);
            cargarDatosFormulario(model);
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }
        return formView;
    }

    @GetMapping("/{id}/ver")
    public String ver(@PathVariable ID id, Model model) {
        try {
            DTO dto = obtenerPorId(id);
            model.addAttribute("form", convertirAFormulario(dto));
            model.addAttribute("isDisabled", true);
            model.addAttribute("titleForm", titleForm);
            cargarDatosFormulario(model);
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
        }
        return formView;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("form") FORM form,
                          BindingResult result,
                          RedirectAttributes attributes,
                          Model model) {
        if (result.hasErrors()) {
            try {
                cargarDatosFormulario(model);
            } catch (ErrorServiceException e) {
                model.addAttribute("msgError", e.getMessage());
            }
            model.addAttribute("titleForm", titleForm);
            return formView;
        }

        try {
            ID id = obtenerIdFormulario(form);
            if (id == null) {
                crearRegistro(form);
            } else {
                actualizarRegistro(id, form);
            }
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return redirectList;
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            try {
                cargarDatosFormulario(model);
            } catch (ErrorServiceException ignored) {}
            model.addAttribute("titleForm", titleForm);
            return formView;
        }
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable ID id, RedirectAttributes attributes) {
        try {
            eliminarRegistro(id);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return redirectList;
    }

    protected void postListar(Model model, List<DTO> items) throws ErrorServiceException {}

    protected void cargarDatosFormulario(Model model) throws ErrorServiceException {}

    protected abstract List<DTO> obtenerListado() throws ErrorServiceException;

    protected abstract DTO obtenerPorId(ID id) throws ErrorServiceException;

    protected abstract void crearRegistro(FORM form) throws ErrorServiceException;

    protected abstract void actualizarRegistro(ID id, FORM form) throws ErrorServiceException;

    protected abstract void eliminarRegistro(ID id) throws ErrorServiceException;

    protected abstract FORM crearFormularioVacio() throws ErrorServiceException;

    protected abstract FORM convertirAFormulario(DTO dto) throws ErrorServiceException;

    protected abstract ID obtenerIdFormulario(FORM form);
}
