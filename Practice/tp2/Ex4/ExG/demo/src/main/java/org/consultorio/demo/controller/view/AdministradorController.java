package org.consultorio.demo.controller.view;

import jakarta.servlet.http.HttpSession;
import org.consultorio.demo.bussiness.domain.FotoPaciente;
import org.consultorio.demo.bussiness.domain.Medico;
import org.consultorio.demo.bussiness.domain.Paciente;
import org.consultorio.demo.bussiness.domain.Usuario;
import org.consultorio.demo.bussiness.domain.enums.Rol;
import org.consultorio.demo.bussiness.logic.service.FotoPacienteService;
import org.consultorio.demo.bussiness.logic.service.MedicoService;
import org.consultorio.demo.bussiness.logic.service.PacienteService;
import org.consultorio.demo.bussiness.logic.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdministradorController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private FotoPacienteService fotoPacienteService;

    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }

        List<Paciente> pacientes = pacienteService.listarTodos();
        List<Medico> medicos = medicoService.listarTodos();

        model.addAttribute("usuario", usuario);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("medicos", medicos);

        return "administrador";
    }

    @PostMapping("/paciente/crear")
    public String crearPaciente(@RequestParam String nombre,
                                @RequestParam String apellido,
                                @RequestParam String documento,
                                @RequestParam MultipartFile foto,
                                @RequestParam String nombreUsuario,
                                @RequestParam String clave,
                                RedirectAttributes redirectAttributes) {
        try {
            // Crear usuario
            Usuario usuario = new Usuario();
            FotoPaciente fotoPaciente = new FotoPaciente();
            fotoPaciente.setNombre(foto.getOriginalFilename());
            fotoPaciente.setContenido(foto.getBytes());

            usuario.setNombreUsuario(nombreUsuario);
            usuario.setClave(clave);
            usuario.setRol(Rol.PACIENTE);
            usuario = usuarioService.crear(usuario);

            // Crear paciente
            Paciente paciente = new Paciente();
            paciente.setNombre(nombre);
            paciente.setApellido(apellido);
            paciente.setDocumento(documento);
            paciente.setUsuario(usuario);
            fotoPaciente.setPaciente(paciente);
            pacienteService.crear(paciente);
            fotoPacienteService.crear(fotoPaciente);

            redirectAttributes.addFlashAttribute("exito", "Paciente creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear paciente: " + e.getMessage());
        }

        return "redirect:/admin/inicio";
    }

    @GetMapping("/paciente/eliminar/{id}")
    public String eliminarPaciente(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            pacienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Paciente desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar paciente: " + e.getMessage());
        }

        return "redirect:/admin/inicio";
    }

    @PostMapping("/medico/crear")
    public String crearMedico(@RequestParam String nombre,
                              @RequestParam String apellido,
                              @RequestParam String documento,
                              @RequestParam String nombreUsuario,
                              @RequestParam String clave,
                              RedirectAttributes redirectAttributes) {
        try {
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(nombreUsuario);
            usuario.setClave(clave);
            usuario.setRol(Rol.MEDICO);
            usuario = usuarioService.crear(usuario);

            // Crear médico
            Medico medico = new Medico();
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setDocumento(documento);
            medico.setUsuario(usuario);
            medicoService.crear(medico);

            redirectAttributes.addFlashAttribute("exito", "Médico creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear médico: " + e.getMessage());
        }

        return "redirect:/admin/inicio";
    }

    @GetMapping("/medico/eliminar/{id}")
    public String eliminarMedico(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            medicoService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Médico desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar médico: " + e.getMessage());
        }

        return "redirect:/admin/inicio";
    }
}
