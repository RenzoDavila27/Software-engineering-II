package com.fioritech.car.components;

import com.fioritech.car.bussiness.dto.JwtResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtSessionManager {

    private static final String TOKEN_TYPE_ATTR = "jwt.tokenType";
    private static final String ACCESS_TOKEN_ATTR = "jwt.accessToken";
    private static final String REFRESH_TOKEN_ATTR = "jwt.refreshToken";

    public void storeTokens(HttpServletRequest request, JwtResponse jwtResponse) {
        if (jwtResponse == null || request == null) {
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(TOKEN_TYPE_ATTR, jwtResponse.getTokenType());
        session.setAttribute(ACCESS_TOKEN_ATTR, jwtResponse.getAccessToken());
        session.setAttribute(REFRESH_TOKEN_ATTR, jwtResponse.getRefreshToken());
    }

    public Optional<String> getAuthorizationHeader(HttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        String tokenType = (String) session.getAttribute(TOKEN_TYPE_ATTR);
        String accessToken = (String) session.getAttribute(ACCESS_TOKEN_ATTR);
        if (StringUtils.hasText(tokenType) && StringUtils.hasText(accessToken)) {
            return Optional.of(tokenType + " " + accessToken);
        }
        return Optional.empty();
    }

    public void clearTokens(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(TOKEN_TYPE_ATTR);
            session.removeAttribute(ACCESS_TOKEN_ATTR);
            session.removeAttribute(REFRESH_TOKEN_ATTR);
        }
    }
}
