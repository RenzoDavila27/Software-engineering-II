package org.consultorio.demo.controller.view;

import jakarta.servlet.http.HttpSession;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.consultorio.demo.bussiness.domain.enums.Rol;
import org.consultorio.demo.bussiness.logic.error.ServiceException;
import org.consultorio.demo.bussiness.logic.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username, 
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.autenticar(username, password);
            session.setAttribute("usuario", usuario);
            
            // Redirigir según el rol
            if (usuario.getRol() == Rol.PACIENTE) {
                return "redirect:/paciente/inicio";
            } else if (usuario.getRol() == Rol.MEDICO) {
                return "redirect:/medico/inicio";
            } else if (usuario.getRol() == Rol.ADMINISTRADOR) {
                return "redirect:/admin/inicio";
            }
            
            return "redirect:/";
        } catch (ServiceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada exitosamente");
        return "redirect:/login";
    }
}
