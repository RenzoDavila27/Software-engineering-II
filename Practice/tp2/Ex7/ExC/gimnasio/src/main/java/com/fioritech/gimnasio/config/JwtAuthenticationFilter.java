package com.fioritech.gimnasio.config;

import com.fioritech.gimnasio.config.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer el token de la Cookie
        final String jwt = extractTokenFromCookie(request);
        final String username;

        if (jwt == null) {
            filterChain.doFilter(request, response); // No hay token, continuar
            return;
        }

        try {
            username = jwtService.extractUsername(jwt);

            // Si hay token PERO el usuario no está autenticado en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Si el token es válido, autenticar al usuario
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No usamos credenciales
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Guardar la autenticación en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Loguear el error si la validación del token falla
            logger.warn("Error al procesar el token JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Método de utilidad para leer la cookie 'jwt_token'
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("jwt_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Define en qué rutas este filtro NO debe ejecutarse.
     * Es crucial para que /login, /logout y los archivos estáticos funcionen.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Lista de rutas públicas exactas
        final List<String> publicPaths = List.of(
                "/", "/login", "/registro", "/logout", "/error" 
        );

        // Lista de prefijos de rutas estáticas
        final List<String> staticPrefixes = List.of(
                "/css/", "/js/", "/img/", "/vendor/", "/scss/"
        );

        boolean isPublicPath = publicPaths.contains(path);
        boolean isStaticPath = staticPrefixes.stream().anyMatch(path::startsWith);

        // Si es pública O estática, el filtro NO debe correr (devuelve true)
        return isPublicPath || isStaticPath;
    }
}
