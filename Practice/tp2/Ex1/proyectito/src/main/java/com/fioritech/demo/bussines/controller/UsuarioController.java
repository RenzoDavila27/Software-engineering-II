package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import com.fioritech.demo.bussines.logic.exception.BusinessException;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuario/listar")
    public String listarUsuarios(Model model) {
        model.addAttribute("listaUsuario", usuarioService.listarUsuarios());
        return "usuario/listar";
    }

    @GetMapping("/usuario/crearForm")
    public String crearUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/crearUsuario";
    }

    @PostMapping("/usuario/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.crearUsuario(usuario);
        return "redirect:/usuario/listar";
    }

    @GetMapping("/usuario/modificarForm/{id}")
    public String modificarUsuarioForm(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarUsuarioPorId(id));
        return "usuario/modificar";
    }

    @PostMapping("/usuario/modificar/{id}")
    public String modificarUsuario(@PathVariable Long id, @ModelAttribute Usuario cambios) {
        usuarioService.modificarUsuario(id, cambios);
        return "redirect:/usuario/listar";
    }

    @GetMapping("/usuario/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuario/listar";
    }

    @PostMapping("/usuario/login")
	public String loginUsuario(@RequestParam(value = "cuenta") String cuenta,@RequestParam(value = "clave") String clave, ModelMap modelo, HttpSession session) {

		try {

			Usuario usuario = usuarioService.login(cuenta, clave);
			session.setAttribute("usuarioSession", usuario);
			return "inicio";

		} catch (BusinessException ex) {
			modelo.put("msgError", ex.getMessage());
			return "login";
		} catch (Exception e) {
			e.printStackTrace();
			modelo.put("msgError", e.getMessage());
			return "login";
		}

	}

    @GetMapping("/usuario/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }
}
