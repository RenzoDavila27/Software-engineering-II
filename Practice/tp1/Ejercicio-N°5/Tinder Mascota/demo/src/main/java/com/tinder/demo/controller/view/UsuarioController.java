package com.tinder.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class UsuarioController {

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private UsuarioService service;

    @PostMapping("/usuario/guardar") //en el form de registro
    public String guardar(@RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam MultipartFile foto,@RequestParam Zona zona,@RequestParam String clave1, @RequestParam String clave2, Model model) throws ErrorServiceException{

        try {
            byte[] imagenBytes = foto.getBytes();
            service.crearUsuario(nombre,apellido,mail,imagenBytes,clave1,zona);
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
    public String verificar(@RequestParam String mail,@RequestParam String clave, Model model) throws ErrorServiceException{


        try{
            service.verificarUsuario(mail,clave);
            model.addAttribute("exito", "se ha logueado correctamente");
            return "perfil"; // va hacia el inicio de la pag una vez logueado

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
