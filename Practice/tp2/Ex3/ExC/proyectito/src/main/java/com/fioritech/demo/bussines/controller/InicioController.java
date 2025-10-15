package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.controller.template.SimpleTemplateController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController extends SimpleTemplateController {

    @GetMapping("/")
    public String inicio(Model model) {
        return render(model);
    }

    @Override
    protected String obtenerVista() {
        return "login";
    }
}

