package com.fioritech.gimnasio.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/usuario/login")
	public String loginUsuario(@RequestParam(value = "cuenta") String cuenta,@RequestParam(value = "clave") String clave, ModelMap modelo, HttpSession session) {

		try {

			Usuario usuario = usuarioService.login(cuenta, clave);
			session.setAttribute("usuarioSession", usuario);
            session.setAttribute("rol", usuario.getRol().name());

			return "view/inicio";

		} catch (BusinessException ex) {
			modelo.put("msgError", ex.getMessage());
			return "view/login";
		} catch (Exception e) {
			e.printStackTrace();
			modelo.put("msgError", e.getMessage());
			return "view/login";
		}

	}

    @GetMapping("/usuario/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "view/login";
    }
    
}
