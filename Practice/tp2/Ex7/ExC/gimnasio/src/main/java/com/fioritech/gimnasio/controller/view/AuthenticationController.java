package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.config.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor // Inyecta las dependencias final
public class AuthenticationController {

    // Inyectados desde tu ApplicationConfig
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtService jwtService;
    
    /**
     * Procesa el intento de login.
     * Reemplaza la lógica de HttpSession por la creación de una cookie JWT.
     */
    @PostMapping("/login")
    public String loginUsuario(
            @RequestParam(value = "cuenta") String cuenta,
            @RequestParam(value = "clave") String clave,
            HttpServletResponse response, // Para poder añadir la cookie
            RedirectAttributes redirectAttrs // Para enviar errores en la redirección
    ) {

        try {
            // 1. Autenticar al usuario usando el manager de Spring
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cuenta, clave)
            );

            // 2. Si tiene éxito, obtener los UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 3. Generar el token JWT
            String jwtToken = jwtService.generateToken(userDetails);

            // 4. Crear la cookie
            Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
            jwtCookie.setHttpOnly(true); // Impide acceso desde JavaScript
            jwtCookie.setPath("/");      // Disponible en todo el sitio
            
            // 5. Añadir la cookie a la respuesta
            response.addCookie(jwtCookie);

            // 6. Redirigir al inicio
            return "redirect:/view/inicio"; // O la URL de inicio que prefieras

        } catch (BadCredentialsException ex) {
            // 7. Si las credenciales son incorrectas
            redirectAttrs.addFlashAttribute("msgError", "Usuario o clave incorrectos.");
            return "redirect:/login"; // Redirige de vuelta al login
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("msgError", "Error inesperado en el login.");
            return "redirect:/login";
        }
    }

    /**
     * Procesa el logout.
     * Reemplaza session.invalidate() por el borrado de la cookie JWT.
     */
    @GetMapping("/logout") // Coincide con SecurityConfig
    public String logout(HttpServletResponse response) {
        
        // 1. Crear una cookie que sobreescriba la existente y la borre
        Cookie jwtCookie = new Cookie("jwt_token", null); // Valor nulo
        jwtCookie.setMaxAge(0); // ¡Expira inmediatamente!
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");

        // 2. Añadir la cookie a la respuesta
        response.addCookie(jwtCookie);

        // 3. Redirigir a la página de login
        return "redirect:/login";
    }
}
