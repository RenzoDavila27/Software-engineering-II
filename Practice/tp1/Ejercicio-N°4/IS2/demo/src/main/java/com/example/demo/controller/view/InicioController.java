package com.example.demo.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

	
	@GetMapping("/i")
	public String inicio() {
		return "view/index";
	}
}