package com.tinder.demo.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tinder.demo.bussines.domain.Usuario;
import com.tinder.demo.bussines.domain.Zona;
import com.tinder.demo.bussines.logic.error.ErrorServiceException;
import com.tinder.demo.bussines.logic.service.JwtService;
import com.tinder.demo.bussines.logic.service.UsuarioService;
import com.tinder.demo.bussines.logic.service.ZonaService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;

@Controller
public class UsuarioController {

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private UsuarioService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService; // El Bean de ApplicationConfig

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/usuario/guardar") //en el form de registro
    public String guardar(@RequestParam String nombre,@RequestParam String apellido,@RequestParam String mail,@RequestParam MultipartFile foto,@RequestParam Zona zona,@RequestParam String clave1, @RequestParam String clave2, ModelMap model, RedirectAttributes redirectAttrs, HttpServletResponse response) throws ErrorServiceException{

        try {
            byte[] imagenBytes = foto.getBytes();
            String fotoTipo = foto.getContentType();
            service.crearUsuario(nombre,apellido,mail,imagenBytes,fotoTipo,clave1,clave2,zona);
            UserDetails userDetails = userDetailsService.loadUserByUsername(mail);
            String jwtToken = jwtService.generateToken(userDetails);

            Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
            jwtCookie.setHttpOnly(true);       // Impide acceso desde JavaScript
            jwtCookie.setPath("/");          // Disponible en todo el sitio
            jwtCookie.setMaxAge(60 * 60 * 24); // Expira en 1 día (en segundos)
            model.put("titulo","Usuario guardado correctamente");
            response.addCookie(jwtCookie); // Añadimos la cookie a la respuesta
            return "redirect:/inicio"; // va hacia la pagina de exito

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
    public String verificar(@RequestParam String mail,@RequestParam String clave, Model model, HttpServletResponse response, RedirectAttributes redirectAttrs
    ) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(mail, clave));
            // Si la autenticación fue exitosa, obtenemos los detalles del usuario
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // --- CAMBIO 3: Generar el JWT ---
            String jwtToken = jwtService.generateToken(userDetails);

            // --- CAMBIO 4: Crear y añadir la Cookie (en lugar de usar HttpSession) ---
            Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
            jwtCookie.setHttpOnly(true);       // Impide acceso desde JavaScript
            jwtCookie.setPath("/");          // Disponible en todo el sitio
            jwtCookie.setMaxAge(60 * 60 * 24); // Expira en 1 día (en segundos)

            response.addCookie(jwtCookie); // Añade la cookie a la respuesta

            return "redirect:/inicio"; // va hacia el inicio de la pag una vez logueado

        }catch (BadCredentialsException e) {
        // --- CAMBIO 5: Capturar la excepción de Spring Security ---
        // Esta es la excepción estándar para email/clave incorrectos
            redirectAttrs.addFlashAttribute("error", "Email o contraseña incorrectos.");
            redirectAttrs.addFlashAttribute("mail", mail); 
            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            redirectAttrs.addFlashAttribute("mail", mail);   // guardo el mail ingresado
            return "redirect:/login";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie jwtCookie = new Cookie("jwt_token", null); 

        // 2. ¡CRÍTICO! Establecer su tiempo de vida en 0 segundos
        //    Esto le dice al navegador que la elimine inmediatamente.
        jwtCookie.setMaxAge(0); 

        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");     // Debe coincidir con el path original

        response.addCookie(jwtCookie);

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
