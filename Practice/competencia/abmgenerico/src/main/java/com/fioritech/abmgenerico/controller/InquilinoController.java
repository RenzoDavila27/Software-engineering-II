package com.example.alquiler.controller;

import com.example.alquiler.entity.Inquilino;
import com.example.alquiler.entity.Sexo;
import com.example.alquiler.entity.TipoDocumento;
import com.example.alquiler.service.InquilinoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/inquilinos")
public class InquilinoController {

    private final InquilinoService service;

    public InquilinoController(InquilinoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("inquilinos", service.listarActivos());
        return "listarInquilini";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("inquilino", new Inquilino());
        model.addAttribute("tiposDoc", TipoDocumento.values());
        model.addAttribute("sexos", Sexo.values());
        return "formInquilino"; // (opcional, si luego quieres crear/editar)
    }

    @PostMapping
    public String guardar(@ModelAttribute @Valid Inquilino inquilino, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("tiposDoc", TipoDocumento.values());
            model.addAttribute("sexos", Sexo.values());
            return "formInquilino";
        }
        service.guardar(inquilino);
        return "redirect:/inquilinos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        service.eliminarLogico(id);
        return "redirect:/inquilinos";
    }
}