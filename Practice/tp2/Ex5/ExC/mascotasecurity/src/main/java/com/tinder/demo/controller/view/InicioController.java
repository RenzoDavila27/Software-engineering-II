package com.tinder.demo.controller.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.logic.service.UsuarioService;
import com.tinder.demo.bussines.logic.service.ZonaService;

@Controller
public class InicioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ZonaService zonaService;

	@GetMapping("/")
	public String inicio() {
		return "index_1";
	}
	    @GetMapping("/inicio")
	    public String inicio(Model model, Authentication authentication) throws ErrorServiceException {
			if (authentication == null || !authentication.isAuthenticated()) {
				return "redirect:/login";
			}

			Usuario usuario = usuarioService.buscarUsuarioPorMail(authentication.getName());

	        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "inicio";
    }

		@GetMapping("/registro")
		public String registro(Model model) throws ErrorServiceException{
			model.addAttribute("zonas", zonaService.buscarZonasActivas());
			return "registro2";
		}

		@PostMapping("/registro")
		public String registrar(
				@RequestParam String nombre,
				@RequestParam String apellido,
				@RequestParam String mail,
				@RequestParam("foto") MultipartFile foto,
				@RequestParam String clave1,
			@RequestParam String clave2,
			@RequestParam Zona zona,
			ModelMap modelo) {

		try {
			byte[] imagenBytes = foto.getBytes();
			String tipoFoto = foto.getContentType();

				usuarioService.crearUsuario(nombre, apellido, mail, imagenBytes, tipoFoto, clave1, clave2, zona);

				modelo.put("exito", "Usuario registrado correctamente");
				return "redirect:/login";

			} catch (ErrorServiceException e) {
				modelo.put("error", e.getMessage());
			modelo.put("nombre", nombre);
			modelo.put("apellido", apellido);
			modelo.put("mail", mail);
			modelo.put("clave1", clave1);
			modelo.put("clave2", clave2);
			modelo.put("zona", zona);

			try {
				modelo.put("zonas", zonaService.buscarZonasActivas());
			} catch (ErrorServiceException zonasEx) {
				Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, "No se pudieron cargar las zonas", zonasEx);
			}

			return "registro2";
		} catch (Exception e) {
			Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, "Error al registrar usuario", e);
			modelo.put("error", "Error inesperado al registrar el usuario");

			try {
				modelo.put("zonas", zonaService.buscarZonasActivas());
			} catch (ErrorServiceException zonasEx) {
				Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, "No se pudieron cargar las zonas", zonasEx);
			}

				return "registro2";
			}

		}

		@GetMapping("/login")
		public String login(@RequestParam(required = false) String error, ModelMap modelo){

			if (error != null){
				modelo.put("error", "Usuario o clave invalidos");
			}

		return "login";
	}
}
