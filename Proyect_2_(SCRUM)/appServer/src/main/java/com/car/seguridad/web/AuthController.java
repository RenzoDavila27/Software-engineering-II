package com.car.seguridad.web;

import com.car.business.domain.enums.RolUsuario;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.UsuarioService;
import com.car.seguridad.authentication.CustomUserDetailsService;
import com.car.seguridad.authentication.UserPrincipal;
import com.car.seguridad.jwt.JwtTokenService;
import com.car.seguridad.web.dto.JwtResponse;
import com.car.seguridad.web.dto.LoginRequest;
import com.car.seguridad.web.dto.OAuthLoginRequest;
import com.car.seguridad.web.dto.RefreshTokenRequest;
import com.car.seguridad.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/seguridad/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService userDetailsService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(buildResponse(principal));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        UsuarioDto dto = new UsuarioDto();
        dto.setNombreUsuario(request.getUsername());
        dto.setClave(request.getPassword());
        dto.setPersonaId(request.getPersonaId());
        dto.setRolUsuario(RolUsuario.CLIENTE);
        try {
            usuarioService.crear(dto);
        } catch (BusinessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(buildResponse(principal));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenService.isRefreshTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
        }
        String username = jwtTokenService.extractUsername(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin usuario"));
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(buildResponse(principal));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/oauth-login")
    public ResponseEntity<JwtResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        try {
            UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(request.getUsername());
            return ResponseEntity.ok(buildResponse(principal));
        } catch (UsernameNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex);
        }
    }

    private JwtResponse buildResponse(UserPrincipal principal) {
        String accessToken = jwtTokenService.generateAccessToken(principal);
        String refreshToken = jwtTokenService.generateRefreshToken(principal);
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return JwtResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenService.getAccessTokenValiditySeconds())
                .roles(roles)
                .build();
    }
}
