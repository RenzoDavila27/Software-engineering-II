package com.tinder.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.logic.service.UsuarioService;
import com.tinder.demo.bussines.logic.service.ZonaService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class UsuarioController {

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private UsuarioService service;

    @PostMapping("/usuario/guardar") //en el form de registro
    public String guardar(@RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam MultipartFile foto,@RequestParam Zona zona,@RequestParam String clave1, @RequestParam String clave2, ModelMap model, RedirectAttributes redirectAttrs) throws ErrorServiceException{

        try {
            byte[] imagenBytes = foto.getBytes();
            String fotoTipo = foto.getContentType();
            service.crearUsuario(nombre,apellido,mail,imagenBytes,fotoTipo,clave1,clave2,zona);
            model.put("titulo","Usuario guardado correctamente");
            return "exito.html"; // va hacia la pagina de exito

        } catch (ErrorServiceException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            redirectAttrs.addFlashAttribute("nombre", nombre);
            redirectAttrs.addFlashAttribute("apellido", apellido);
            redirectAttrs.addFlashAttribute("mail", mail);
            redirectAttrs.addFlashAttribute("foto", foto);
            redirectAttrs.addFlashAttribute("zona", zona);
            redirectAttrs.addFlashAttribute("zonas", zonaService.buscarZonasActivas());
            return "redirect:/registro"; //
        } catch (Exception e) {
            e.printStackTrace(); 
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "redirect:/registro"; //
        }

    }

    @PostMapping("/usuario/loginUsuario") // en el form de login
    public String verificar(@RequestParam String mail,@RequestParam String clave, Model model, HttpSession session, RedirectAttributes redirectAttrs
    ) throws ErrorServiceException {


        try {
            Usuario usuario = service.verificarUsuario(mail, clave);
            session.setAttribute("usuariosession", usuario);
            model.addAttribute("exito", "se ha logueado correctamente");
            return "redirect:/inicio"; // va hacia el inicio de la pag una vez logueado

        } catch (ErrorServiceException e) {

            redirectAttrs.addFlashAttribute("error", e.getMessage());
            redirectAttrs.addFlashAttribute("mail", mail);   // 👉 conserva el mail ingresado
            return "redirect:/login"; // va hacia la pagina login
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            redirectAttrs.addFlashAttribute("mail", mail);   // 👉 guardo el mail ingresado
            return "redirect:/login"; // va hacia la pagina login
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

        model.addAttribute("foto", usuario.getFoto());
        model.addAttribute("id", id);
        model.addAttribute("nombre", usuario.getNombre());
        model.addAttribute("apellido", usuario.getApellido());
        model.addAttribute("mail", usuario.getMail());
        model.addAttribute("zonaUsuario", usuario.getZona());
        model.addAttribute("zonas", zonaService.buscarZonasActivas());

        return "perfil";
    }

    @PostMapping("/usuario/actualizar-perfil")
    public String editarUsuario(@RequestParam Long id,@RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam MultipartFile foto,@RequestParam Zona idZona,@RequestParam String claveActual,@RequestParam String clave1, @RequestParam String clave2, Model model, RedirectAttributes redirectAttrs) throws ErrorServiceException{

        try {
            byte[] imagenBytes = foto.getBytes();
            String tipoFoto = foto.getContentType();
            service.modificarUsuario(id,nombre,apellido,mail,imagenBytes,tipoFoto,claveActual,clave1,clave2,idZona);
            model.addAttribute("exito", "Usuario modificado correctamente");
            return "redirect:/inicio"; // va hacia la pagina de exito

        } catch (ErrorServiceException e) {
            Usuario usuario = service.buscarUsuarioPorId(id);
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            redirectAttrs.addFlashAttribute("perfil", usuario);
            redirectAttrs.addFlashAttribute("nombre", nombre);
            redirectAttrs.addFlashAttribute("apellido", apellido);
            redirectAttrs.addFlashAttribute("mail", mail);
            redirectAttrs.addFlashAttribute("foto", foto);
            redirectAttrs.addFlashAttribute("zonaUsuario", usuario.getZona());
            redirectAttrs.addFlashAttribute("zonas", zonaService.buscarZonasActivas());

            return "redirect:/perfil"; // va hacia la pagina de error
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error inesperado al guardar el usuario");
            return "error"; // va hacia la pagina error
        }

    }

    @GetMapping("usuario/foto/{id}")
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Long id) throws Exception{
        Usuario usuario = service.buscarUsuarioPorId(id);
        if (usuario.getFoto() != null && usuario.getTipoFoto() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(usuario.getTipoFoto()))
                    .body(usuario.getFoto());
        } else {
            return ResponseEntity.notFound().build();  // o podrías devolver una imagen por defecto
        }
    }

}
