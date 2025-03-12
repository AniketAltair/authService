package com.example.macrominder.authService.dto;

import com.example.macrominder.authService.enums.OAuthProvider;

public class SignUpOAuthDTO {

    private String accessToken;
    private OAuthProvider oauthProvider;
    private String role;

    public SignUpOAuthDTO(){

    }

    public SignUpOAuthDTO(String accessToken,
                          OAuthProvider oauthProvider,
                          String role) {
        this.accessToken = accessToken;
        this.oauthProvider = oauthProvider;
        this.role = role;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public OAuthProvider getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(OAuthProvider oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
