package com.fioritech.car.bussiness.dto;

public class OAuthLoginRequest {

    private String username;

    public OAuthLoginRequest() {
    }

    public OAuthLoginRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
