package com.example.frontend.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.frontend.business.domain.VideojuegoDto;
import com.example.frontend.business.logic.error.ErrorServiceException;
import com.example.frontend.business.logic.service.VideojuegoService;

@Controller
public class InicioController {

    private final VideojuegoService videojuegoService;

    public InicioController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        try {
            List<VideojuegoDto> videojuegos = videojuegoService.listar();
            model.addAttribute("listaVideojuego", videojuegos);
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("listaVideojuego", List.of());
        }
        return "view/index";
    }
}
