package com.fioritech.demo.bussines.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class InicioController {

    @GetMapping("/")
	public String inicio() {
		return "login";
	}
}