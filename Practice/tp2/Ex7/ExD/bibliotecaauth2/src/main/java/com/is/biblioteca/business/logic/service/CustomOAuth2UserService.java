package com.is.biblioteca.business.logic.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.domain.enumeration.Rol;
import com.is.biblioteca.business.persistence.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    public CustomOAuth2UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User delegate = super.loadUser(userRequest);
        Map<String, Object> attributes = delegate.getAttributes();

        Usuario usuario = synchronizeUsuario(attributes);

        String email = usuario.getEmail();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Usuario OAuth2 autenticado: {} con authorities {}", email, authorities);
        }

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        setUsuarioEnSesion(usuario);

        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }

    public Usuario synchronizeUsuario(Map<String, Object> attributes) {

        String email = (String) attributes.get("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("missing_email"),
                    "El proveedor OAuth2 no devolvió un email válido");
        }
        String nombre = (String) attributes.getOrDefault("name", attributes.get("given_name"));

        Usuario usuario = usuarioRepository.buscarUsuarioPorEmail(email);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setId(UUID.randomUUID().toString());
            usuario.setEmail(email);
            usuario.setNombre(nombre != null && !nombre.isBlank() ? nombre : email);
            usuario.setRol(Rol.USER);
            usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            usuario.setEliminado(false);
        } else {
            if (nombre != null && !nombre.isBlank() && !nombre.equals(usuario.getNombre())) {
                usuario.setNombre(nombre);
            }
            if (usuario.isEliminado()) {
                usuario.setEliminado(false);
            }
        }

        usuarioRepository.save(usuario);
        return usuario;
    }

    public void setUsuarioEnSesion(Usuario usuario) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = requestAttributes.getRequest().getSession(true);
        session.setAttribute("usuariosession", usuario);
    }
}
