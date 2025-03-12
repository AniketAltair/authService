package com.example.macrominder.authService.dto;

public class ValidateTokenDTO {

    String accessToken;

    public ValidateTokenDTO(){

    }

    public ValidateTokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
