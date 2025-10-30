package com.books.demo.auth.dto;

import java.util.List;

public class JwtResponse {

    private final String token;
    private final String type = "Bearer";
    private final String username;
    private final List<String> roles;

    public JwtResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}

