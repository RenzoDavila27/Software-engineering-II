package com.car.seguridad.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private List<String> roles;
}
