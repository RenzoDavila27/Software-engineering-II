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

import com.car.clientead.business.logic.EmpresaService;
import com.car.clientead.business.logic.PersonaService;
import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.EmpresaDto;
import com.car.clientead.client.dto.PersonaDto;
import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    private static final String LIST_VIEW = "lEmpresa.html";
    private static final String FORM_VIEW = "eEmpresa.html";
    private static final String REDIRECT_EMPRESAS = "redirect:/empresas";

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public String listar(Model model) {
        List<EmpresaDto> empresas;
        try {
            empresas = empresaService.listar();
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("contactoMap", Collections.emptyMap());
            model.addAttribute("personaMap", mapearPersonas());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("titleList", "Gestión de Empresas");
            return LIST_VIEW;
        }
        model.addAttribute("items", empresas);
        model.addAttribute("contactoMap", empresaService.mapearContactos(empresas));
        model.addAttribute("personaMap", mapearPersonas());
        model.addAttribute("titleList", "Gestión de Empresas");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String mostrarAlta(Model model) {
        prepararFormulario(model, new EmpresaDto(), contactoPorDefecto(), "Alta de Empresa", false);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute EmpresaDto dto,
                        @ModelAttribute("contactoCorreo") ContactoCorreoElectronicoDto contactoCorreo,
                        Model model) {
        try {
            empresaService.crear(dto, contactoCorreo);
            return REDIRECT_EMPRESAS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, contactoCorreo, "Alta de Empresa", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            EmpresaDto dto = empresaService.consultar(id);
            ContactoCorreoElectronicoDto contacto = empresaService.obtenerContacto(dto.getContactoId());
            prepararFormulario(model, dto, contacto, "Detalle de Empresa", true);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_EMPRESAS;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            EmpresaDto dto = empresaService.consultar(id);
            ContactoCorreoElectronicoDto contacto = empresaService.obtenerContacto(dto.getContactoId());
            prepararFormulario(model, dto, contacto, "Modificar Empresa", false);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_EMPRESAS;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
                            @ModelAttribute EmpresaDto dto,
                            @ModelAttribute("contactoCorreo") ContactoCorreoElectronicoDto contactoCorreo,
                            Model model) {
        try {
            empresaService.modificar(id, dto, contactoCorreo);
            return REDIRECT_EMPRESAS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, contactoCorreo, "Modificar Empresa", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            empresaService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar empresa: " + ex.getMessage());
        }
        return REDIRECT_EMPRESAS;
    }

    private void prepararFormulario(Model model,
                                    EmpresaDto dto,
                                    ContactoCorreoElectronicoDto contacto,
                                    String titulo,
                                    boolean soloLectura) {
        model.addAttribute("item", dto != null ? dto : new EmpresaDto());
        model.addAttribute("contactoCorreo", contacto != null ? contacto : contactoPorDefecto());
        model.addAttribute("titleForm", titulo);
        model.addAttribute("modoVer", soloLectura);
        model.addAttribute("personas", obtenerPersonas());
        model.addAttribute("tiposContacto", TipoContacto.values());
    }

    private ContactoCorreoElectronicoDto contactoPorDefecto() {
        ContactoCorreoElectronicoDto contacto = new ContactoCorreoElectronicoDto();
        contacto.setTipoContacto(TipoContacto.EMPRESA);
        return contacto;
    }

    private List<PersonaDto> obtenerPersonas() {
        try {
            return personaService.listar();
        } catch (ApiClientException ex) {
            return Collections.emptyList();
        }
    }

    private Map<String, PersonaDto> mapearPersonas() {
        List<PersonaDto> personas = obtenerPersonas();
        if (personas.isEmpty()) {
            return Collections.emptyMap();
        }
        return personas.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(PersonaDto::getId, p -> p, (a, b) -> a));
    }
}
