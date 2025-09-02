package com.tinder.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.logic.service.UsuarioService;
import com.tinder.demo.bussines.logic.service.ZonaService;


@Controller
public class UsuarioController {

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private UsuarioService service;

    @PostMapping("/usuario/guardar") //en el form de registro
    public String guardar(Usuario usuario, Model model) throws ErrorServiceException{

        try {
            model.addAttribute("zonas", zonaService.buscarZonasActivas());
            service.crearUsuario(usuario.getNombre(), usuario.getApellido(),usuario.getMail(),usuario.getFoto(),usuario.getClave(),usuario.getZona());

            model.addAttribute("exito", "Usuario guardado correctamente");
            return "exito"; // va hacia la pagina de exito

        } catch (ErrorServiceException e) {
            
            model.addAttribute("error", e.getMessage());
            return "error"; // va hacia la pagina de error
        } catch (Exception e) {
            e.printStackTrace(); 
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "error"; // va hacia la pagina error
        }

    }

    @GetMapping("/usuario/loginUsuario") // en el form de login
    public String verificar(Usuario usuario, Model model) throws ErrorServiceException{


        try{

            service.verificarUsuario(usuario.getMail(),usuario.getClave());
            model.addAttribute("exito", "se ha logueado correctamente");
            return "inicio"; // va hacia el inicio de la pag una vez logueado

        }catch (ErrorServiceException e) {
            
            model.addAttribute("error", e.getMessage());
            return "error"; // va hacia la pagina de error
        } catch (Exception e) {
            e.printStackTrace(); 
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "error"; // va hacia la pagina error
        }

    }



    
    
    
}
