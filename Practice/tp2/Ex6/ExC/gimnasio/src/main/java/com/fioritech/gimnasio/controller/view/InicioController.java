package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.logic.service.CuotaMensualService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InicioController {

    private final CuotaMensualService cuotaMensualService;

    public InicioController(CuotaMensualService cuotaMensualService) {
        this.cuotaMensualService = cuotaMensualService;
    }

    @GetMapping("/")
    public String index() {
        cuotaMensualService.actualizarCuotasMensuales();
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("msgError", "Usuario o clave incorrecta");
        }
        return "view/login";
    }

    @GetMapping("/inicio")
    public String inicio(HttpSession session) {
        if (session.getAttribute("usuarioSession") == null) {
            return "redirect:/login";
        }
        cuotaMensualService.actualizarCuotasMensuales();
        return "view/inicio";
    }
}
