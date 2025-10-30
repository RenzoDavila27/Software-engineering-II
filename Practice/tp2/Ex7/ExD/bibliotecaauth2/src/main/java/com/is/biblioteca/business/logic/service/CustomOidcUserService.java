package com.is.biblioteca.business.logic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.is.biblioteca.business.domain.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOidcUserService.class);
    private final CustomOAuth2UserService delegate;

    public CustomOidcUserService(CustomOAuth2UserService delegate) {
        this.delegate = delegate;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();

        Usuario usuario = delegate.synchronizeUsuario(attributes);
        delegate.setUsuarioEnSesion(usuario);

        List<GrantedAuthority> authorities = new ArrayList<>(oidcUser.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Usuario OIDC autenticado: {} con authorities {}", usuario.getEmail(), authorities);
        }

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new DefaultOidcUser(
                authorities,
                userRequest.getIdToken(),
                new OidcUserInfo(attributes),
                userNameAttributeName
        );
    }
}
