package com.tinder.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

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
    public String guardar(@RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam MultipartFile foto,@RequestParam Zona zona,@RequestParam String clave1, @RequestParam String clave2, ModelMap model) throws ErrorServiceException{

        try {
            byte[] imagenBytes = foto.getBytes();
            service.crearUsuario(nombre,apellido,mail,imagenBytes,clave1,zona);
            model.put("titulo","Usuario guardado correctamente");
            return "exito.html"; // va hacia la pagina de exito

        } catch (ErrorServiceException e) {
            
            model.addAttribute("error", e.getMessage());
            return "error"; // va hacia la pagina de error
        } catch (Exception e) {
            e.printStackTrace(); 
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "error"; // va hacia la pagina error
        }

    }

    @PostMapping("/usuario/loginUsuario") // en el form de login
    public String verificar(@RequestParam String mail,@RequestParam String clave, Model model, HttpSession session) throws ErrorServiceException {


        try {
            Usuario usuario = service.verificarUsuario(mail, clave);
            session.setAttribute("usuariosession", usuario);
            model.addAttribute("exito", "se ha logueado correctamente");
            return "redirect:/inicio"; // va hacia el inicio de la pag una vez logueado

        } catch (ErrorServiceException e) {

            model.addAttribute("error", e.getMessage());
            return "error"; // va hacia la pagina de error
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "error"; // va hacia la pagina error
        }

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // destruye la sesión actual
        return "redirect:/"; // te manda de vuelta al login
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Long id) throws ErrorServiceException {

        Usuario usuario = service.buscarUsuarioPorId(id);

        if (usuario == null) {
            return "redirect:/login"; // no está logueado
        }

        model.addAttribute("perfil", usuario);
        model.addAttribute("zonas", zonaService.buscarZonasActivas());

        return "perfil";
    }

    @PostMapping("/usuario/actualizar-perfil")
    public String editarUsuario(@RequestParam Long id,@RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam MultipartFile foto,@RequestParam Zona idZona,@RequestParam String clave1, @RequestParam String clave2, Model model) throws ErrorServiceException{

        try {
            byte[] imagenBytes = foto.getBytes();
            service.modificarUsuario(id,nombre,apellido,mail,imagenBytes,clave1,idZona);
            model.addAttribute("exito", "Usuario modificado correctamente");
            return "redirect:/perfil"; // va hacia la pagina de exito

        } catch (ErrorServiceException e) {

            model.addAttribute("error", e.getMessage());
            return "error"; // va hacia la pagina de error
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "error"; // va hacia la pagina error
        }

    }

}
