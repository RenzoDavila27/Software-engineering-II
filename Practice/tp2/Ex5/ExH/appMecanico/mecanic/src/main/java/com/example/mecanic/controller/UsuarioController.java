/* 
package com.example.mecanic.controller;

import com.tinder.demo.bussines.logic.service.UsuarioService;

import main.java.com.example.mecanic.bussines.logic.error.ErrorServiceException;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    protected String viewList="lUsuario.html";
    protected String redirectList="redirect:/usuario/list";
    protected String viewEdit="eUsuario.html";
    protected String titleList = "Lista de Usuarios";
    protected String titleEdit = "Editar Usuario";

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/list")
    public String listar(Model model){
        try{
            model.addAttribute("items",usuarioService.listarUsuariosActivos());
            this.model.addAttribute("titleList", titleList);
            this.model.addAttribute("nameEntityLower", "usuario");
        
      }catch(ErrorServiceException e) {	
    	  this.model.addAttribute("msgError", e.getMessage());  
	  }catch(Exception e) {
		  this.model.addAttribute("msgError", "Error de Sistema");  
	  }
    }


}
*/