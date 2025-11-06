package com.fioritech.gimnasio.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fioritech.gimnasio.business.logic.service.CuotaMensualService;

@Controller
public class InicioController {

	@Autowired
	private CuotaMensualService CuotaMensualService;

    @GetMapping("/")
	public String inicio() {
		CuotaMensualService.actualizarCuotasMensuales();
		return "view/login";
	}
}
