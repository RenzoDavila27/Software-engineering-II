/* 

package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.Mecanico;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.mecanic.controller.BaseController;
import org.springframework.stereotype.Controller;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;
import com.example.mecanic.bussines.logic.service.MecanicoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.example.mecanic.bussines.domain.entity.Usuario;
import com.example.mecanic.bussines.persistence.MecanicoRepository;
import com.example.mecanic.bussines.logic.service.UsuarioService;

@Controller
@RequestMapping("/mecanico")
public class MecanicoController{

    @Autowired
    private MecanicoService mecanicoService;

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") Mecanico mecanico,
                             @RequestParam("Clave") String clave,
                             @RequestParam("Nombre") String nombre,
                             @RequestParam("repetirClave") String repetirClave,
                             RedirectAttributes attributes,
                             Model model) {

        try {
            this.model = model;


            // 🔹 Alta o modificación según corresponda
            if (mecanico.getId() == null) {
                mecanicoService.alta(mecanico,nombre,clave,repetirClave);
            } else {
                mecanicoService.modificar(mecanico.getId(), mecanico,nombre,clave,repetirClave);
            }
            attributes.addFlashAttribute("msgExito", "Mecánico guardado correctamente.");
            return redirectList;

        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("item", mecanico);
            model.addAttribute("isDisabled", false);
            return viewEdit;
        } catch (Exception e) {
            model.addAttribute("msgError", "Error del sistema");
            model.addAttribute("item", mecanico);
            model.addAttribute("isDisabled", false);
            return viewEdit;
        }
    }
}
    
*/
