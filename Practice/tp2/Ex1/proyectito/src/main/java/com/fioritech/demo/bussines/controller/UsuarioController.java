package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuario/listar";
    }

    @GetMapping("/crear")
    public String crearUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/crear";
    }

    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.crearUsuario(usuario);
        return "redirect:/usuario/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarUsuarioForm(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarUsuarioPorId(id));
        return "usuario/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarUsuario(@PathVariable Long id, @ModelAttribute Usuario cambios) {
        usuarioService.modificarUsuario(id, cambios);
        return "redirect:/usuario/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuario/listar";
    }
}
