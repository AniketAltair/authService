package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.dto.ValidateTokenDTO;
import com.example.macrominder.authService.enums.AuthType;
import com.example.macrominder.authService.models.UserInfo;
import com.example.macrominder.authService.services.UserService;
import com.example.macrominder.authService.services.UserServiceImp;
import com.example.macrominder.authService.services.ValidateTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class ValidationController {

    private final ValidateTokenService validateTokenService;
    private final UserService userService;

    @Autowired
    public ValidationController(ValidateTokenService validateTokenService,
                                UserService userService) {
        this.validateTokenService = validateTokenService;
        this.userService = userService;
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {

        // Check if Authorization header is missing or not in Bearer format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        // Extract token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        // Validate the token
        boolean isValid = validateTokenService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse responseobj) {
        Map<String, Object> response = new HashMap<>();
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token is missing");
        }

        Optional<Claims> tokenOptional = validateTokenService.validateRefreshToken(refreshToken);

        if (tokenOptional.get().getSubject().isEmpty()) {
            return ResponseEntity.status(403).body("Invalid or expired refresh token");
        }

        String email = tokenOptional.get().getSubject();
        String authType = tokenOptional.get().get("authType",String.class);

        Optional<UserInfo> userInfo = Optional.of(new UserInfo());


        if(authType.equals(AuthType.GOOGLEEMAIL.toString())){
            userInfo = userService.findGoogleEmail(email);
        }else if(authType.equals(AuthType.FACEBOOKEMAIL.toString())){
            userInfo = userService.findFacebookEmail(email);
        }else{
            userInfo = userService.findEmail(email);
        }

        if(userInfo.isPresent() && (!userInfo.get().getRefreshToken().getUserRefreshToken().equals(refreshToken))){
            return ResponseEntity.status(403).body("Token reuse detected, force logout");
        }

        // also pass here which type of auth, get this from refresh token.
        response = userService.updateTokens(userInfo.get(),responseobj,authType);

        return ResponseEntity.ok(response);
    }
}
