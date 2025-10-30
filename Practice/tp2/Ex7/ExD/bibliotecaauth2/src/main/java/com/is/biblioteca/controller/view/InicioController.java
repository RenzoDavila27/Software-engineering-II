package com.is.biblioteca.controller.view;


import com.is.biblioteca.business.logic.error.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.logic.service.InicioAplicacionService;
import com.is.biblioteca.business.logic.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class InicioController {

	@Autowired
   	private InicioAplicacionService inicioAplicacionService;

	@Autowired
	private UsuarioService usuarioService;
	
    @GetMapping("/")
	public String index() {
    	
       //Creo el usuario por defecto	
 	   try {	
 		inicioAplicacionService.iniciarAplicacion();
 	   }catch(Exception e) {}	
    	
	   return "index.html";
	}
	
	@GetMapping("/registrar")
	public String registrar() {
	   return "registro.html";
	}

	@PostMapping("/registro")
	public String registro(@RequestParam String nombre, @RequestParam String email,
			@RequestParam String password, @RequestParam String password2, @RequestParam MultipartFile archivo,  ModelMap modelo) {
	   try {
		   usuarioService.crearUsuario(nombre, email, password, password2, archivo);
		   modelo.put("exito", "Usuario registrado correctamente");
		   return "index.html";
		} catch(ErrorServiceException e) {
		   modelo.put("error", e.getMessage());
		   modelo.put("nombre", nombre);
		   modelo.put("email", email);
		   return "registro.html";
		}
	}

	@GetMapping("/login")
	public String login(@RequestParam(required = false) String error,
						ModelMap modelo) {
		if (error != null) {
			modelo.put("error", "Usuario o clave incorrecta");}
	   return "login.html";
	}

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/inicio")
    public String inicio(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        if (usuario != null) {
            if (usuario.getRol().toString().equals("ADMIN")) {
                return "redirect:/admin/dashboard";
            }else {
                return "inicio";
            }
        }else {
            return "index";
        }
    }

    @GetMapping("/regresoPage")
    public String regreso() {
        return "redirect:/inicio";
    }
}
