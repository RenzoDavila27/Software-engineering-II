package org.consultorio.demo.controller.view;

import jakarta.servlet.http.HttpSession;
import org.consultorio.demo.bussiness.domain.DetalleHistoriaClinica;
import org.consultorio.demo.bussiness.domain.Medico;
import org.consultorio.demo.bussiness.domain.Paciente;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.consultorio.demo.bussiness.logic.service.DetalleHistoriaClinicaService;
import org.consultorio.demo.bussiness.logic.service.MedicoService;
import org.consultorio.demo.bussiness.logic.service.PacienteService;
import org.consultorio.demo.bussiness.logic.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private DetalleHistoriaClinicaService detalleHistoriaClinicaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Paciente> pacientes = pacienteService.listarActivos();
        List<DetalleHistoriaClinica> detalles = detalleHistoriaClinicaService.listarActivos();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("detalles", detalles);
        
        return "medico";
    }

    @PostMapping("/detalle/guardar")
    public String guardarDetalle(@RequestParam String pacienteId,
                                 @RequestParam String fecha,
                                 @RequestParam String detalle,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Buscar el médico asociado al usuario
            List<Medico> medicos = medicoService.listarActivos();
            Medico medico = medicos.stream()
                    .filter(m -> m.getUsuario() != null && m.getUsuario().getId().equals(usuario.getId()))
                    .findFirst()
                    .orElse(null);

            if (medico == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontró el médico asociado");
                return "redirect:/medico/inicio";
            }

            DetalleHistoriaClinica detalleHC = new DetalleHistoriaClinica();
            detalleHC.setFechaHistoria(LocalDate.parse(fecha));
            detalleHC.setDetalleHistoria(detalle);
            detalleHC.setMedico(medico);

            detalleHistoriaClinicaService.crear(detalleHC);

            redirectAttributes.addFlashAttribute("exito", "Detalle guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar detalle: " + e.getMessage());
        }

        return "redirect:/medico/inicio";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String documento,
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

        return "redirect:/medico/inicio";
    }
}
