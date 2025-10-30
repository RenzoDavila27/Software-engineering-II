package com.tinder.demo.bussines.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.tinder.demo.bussines.logic.service.JwtService;
import jakarta.servlet.http.Cookie;
import java.util.List;


import java.io.IOException;

@Component
@RequiredArgsConstructor // Crea un constructor con los campos final
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Nuestra implementación

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException{

        final String jwt = extractTokenFromCookie(request);
        final String userEmail;

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extraer el userEmail (subject) del token
            userEmail = jwtService.extractUsername(jwt);

            // 4. Comprobar si el email no es nulo Y si el usuario NO está ya autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        
                // 5. Cargar los UserDetails de la BD
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // 7. AUTENTICAR
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error al validar el token JWT", e);
        }
        
        // 11. Pasar la petición al siguiente filtro
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            // Busca la cookie con el nombre que definiremos en el login
            if ("jwt_token".equals(cookie.getName())) { 
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Lista de rutas públicas exactas
        // ¡Añadimos /logout aquí!
        final List<String> publicPaths = List.of(
            "/", "/index_1", 
            "/registro", "/usuario/guardar",
            "/login", "/usuario/loginUsuario",
            "/logout"
        );

        // Lista de prefijos de rutas estáticas (CSS, JS, etc.)
        final List<String> staticPrefixes = List.of(
            "/css/", "/js/", "/img/", "/vendor/"
        );

        // Comprueba si la ruta es una de las públicas
        boolean isPublicPath = publicPaths.contains(path);

        // Comprueba si la ruta empieza con uno de los prefijos estáticos
        boolean isStaticPath = staticPrefixes.stream().anyMatch(path::startsWith);

        // Si es pública O estática, el filtro NO debe correr (devuelve true)
        return isPublicPath || isStaticPath;
    }
}