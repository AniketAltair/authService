package com.example.macrominder.authService.services;

import io.jsonwebtoken.Claims;

import java.util.Optional;

public interface ValidateTokenService {
    public boolean validateToken(String token);
    public Optional<Claims> validateRefreshToken(String token);
}
