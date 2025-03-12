package com.example.macrominder.authService.services;

import com.example.macrominder.authService.dto.ValidateTokenDTO;
import com.example.macrominder.authService.enums.AuthType;
import com.example.macrominder.authService.models.UserDetails;
import com.example.macrominder.authService.models.UserInfo;
import com.example.macrominder.authService.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class ValidateTokenServiceImp implements ValidateTokenService {

    private final Key key;
    private final UserRepository userRepository;

    public ValidateTokenServiceImp(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("JWT secret key is missing!");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.userRepository = userRepository;
        System.out.println("JWT Secret Key Loaded Successfully!");
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userEmail = claims.getSubject();
            Optional<UserInfo> user = Optional.of(new UserInfo());

            System.out.println("userEmail : "+userEmail);
            // get authtype here from token
            if(claims.get("authType",String.class).equals(AuthType.GOOGLEEMAIL.toString())){
                user = userRepository.findByGoogleEmail(userEmail);
            }else if(claims.get("authType",String.class).equals(AuthType.FACEBOOKEMAIL.toString())){
                user = userRepository.findByFacebookEmail(userEmail);
            }else{
                user = userRepository.findByEmail(userEmail);
            }

            return user.isPresent() && claims.getExpiration().after(new Date());

        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (JwtException e) { // Catches multiple JWT-related exceptions
            System.out.println("Invalid token: " + e.getMessage());
        }
        return false;
    }

    public Optional<Claims> validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.of(claims); // ✅ Return the entire claims object

        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            return Optional.empty(); // Token is invalid or expired
        }
    }
}
