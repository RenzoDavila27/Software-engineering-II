package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.MigracionService;
import com.fioritech.demo.bussines.logic.service.dto.MigracionResultado;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/migracion")
public class MigracionController {

    private final MigracionService migracionService;

    public MigracionController(MigracionService migracionService) {
        this.migracionService = migracionService;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        if (!model.containsAttribute("resultado")) {
            model.addAttribute("resultado", null);
        }
        return "migracion/importar";
    }

    @PostMapping("/proveedores")
    public String migrarProveedores(@RequestParam("archivo") MultipartFile archivo,
                                    RedirectAttributes redirectAttributes) {
        try {
            MigracionResultado resultado = migracionService.migrarProveedores(archivo);
            redirectAttributes.addFlashAttribute("resultado", resultado);
            redirectAttributes.addFlashAttribute("msgExito",
                    "Migración finalizada. Se procesaron " + resultado.getRegistrosProcesados()
                            + " registro(s).");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("msgError", ex.getMessage());
        }
        return "redirect:/migracion";
    }
}
