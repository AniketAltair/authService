package com.example.macrominder.authService.dto;

import com.example.macrominder.authService.enums.OAuthProvider;

public class SignInOAuthDTO {
    private String accessToken;
    private OAuthProvider oauthProvider;

    public SignInOAuthDTO(){

    }

    public SignInOAuthDTO(String accessToken, OAuthProvider oauthProvider) {
        this.accessToken = accessToken;
        this.oauthProvider = oauthProvider;
    }

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
}
