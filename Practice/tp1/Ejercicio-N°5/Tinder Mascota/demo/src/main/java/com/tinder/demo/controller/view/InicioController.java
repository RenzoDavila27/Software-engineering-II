package com.tinder.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.logic.service.ZonaService;

@Controller
public class InicioController {

	@Autowired
	private ZonaService zonaService;

	@GetMapping("/")
	public String inicio() {
		return "index_1";
	}
	@GetMapping("/registro")
	public String registro(Model model) throws ErrorServiceException{
		 model.addAttribute("zonas", zonaService.buscarZonasActivas());
		return "registro";
	}

	@GetMapping("/login")
	public String login(){
		return "login";
	}

	/* 
	@GetMapping("/registro/cargar")
	public String registro(Model model)throws ErrorServiceException{

		Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("zonas", zonaService.buscarZonasActivas());
        return "registro";
	}
	*/
}