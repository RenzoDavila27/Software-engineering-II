package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.Mecanico;
import com.example.mecanic.bussines.domain.enumeration.Rol;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.logic.service.MecanicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mecanico")
public class MecanicoController {

    @Autowired
    private MecanicoService mecanicoService;

    // Alta y modificación
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") Mecanico mecanico,
                             @RequestParam("nombreUsuario") String nombreUsuario,
                             @RequestParam("clave") String clave,
                             @RequestParam("repetirClave") String repetirClave,
                             @RequestParam("rol") Rol rol,
                             RedirectAttributes attributes,
                             Model model) {
        try {
            if (mecanico.getId() == null) {
                mecanicoService.alta(mecanico, nombreUsuario, clave, repetirClave, rol);
            } else {
                mecanicoService.modificar(mecanico.getId(), mecanico, nombreUsuario, clave, repetirClave, rol);
            }
            attributes.addFlashAttribute("msgExito", "Mecánico guardado correctamente.");
            return "redirect:/mecanico/list";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("item", mecanico);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("isDisabled", false);
            return "eMecanico.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error del sistema");
            model.addAttribute("item", mecanico);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("isDisabled", false);
            return "eMecanico.html";
        }
    }

    // Formulario de alta
    @GetMapping("/alta")
    public String crear(Mecanico mecanico, Model model) {
        model.addAttribute("item", mecanico);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("isDisabled", false);
        return "eMecanico.html";
    }

    // Listado
    @GetMapping("/list")
    public String listar(Model model) {
        try {
            model.addAttribute("items", mecanicoService.listarActivos());
            return "lMecanico.html";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "inicio.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error del sistema");
            return "inicio.html";
        }
    }

    // Modificación
    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        try {
            Mecanico mecanico = mecanicoService.obtenerMecanico(id);
            model.addAttribute("item", mecanico);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("isDisabled", false);
            return "eMecanico.html";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "eMecanico.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "eMecanico.html";
        }
    }

    // Baja
    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attributes, Model model) {
        try {
            mecanicoService.eliminar(id);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/mecanico/list";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            return "eMecanico.html";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "eMecanico.html";
        }
    }
}
