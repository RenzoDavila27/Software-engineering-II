package org.consultorio.demo.controller.view;

import jakarta.servlet.http.HttpSession;
import org.consultorio.demo.bussiness.domain.FotoPaciente;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.consultorio.demo.bussiness.logic.service.HistoriaClinicaService;
import org.consultorio.demo.bussiness.logic.service.PacienteService;
import org.consultorio.demo.bussiness.logic.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        FotoPaciente fotoPaciente = usuarioService.buscarFoto(usuario);
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("foto", fotoPaciente);
        return "paciente";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String documento,
                                   @RequestParam MultipartFile foto,
                                   @RequestParam(required = false) String password,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            if (password != null && !password.isEmpty()) {
                usuario.setClave(password);
            }
            usuarioService.modificar(usuario);

            redirectAttributes.addFlashAttribute("exito", "Perfil actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/paciente/inicio";
    }
}
