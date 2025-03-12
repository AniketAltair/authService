package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.dto.SignUpDTO;
import com.example.macrominder.authService.dto.SignUpOAuthDTO;
import com.example.macrominder.authService.enums.OAuthProvider;
import com.example.macrominder.authService.services.UserService;
import com.example.macrominder.authService.services.UserServiceImp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SignUpController {

    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDTO signUpDTO, HttpServletResponse responseobj) {
        Map<String, Object> response = userService.registerUser(signUpDTO,responseobj);

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signupoauth")
    public ResponseEntity<?> signUpOAuth(@RequestBody SignUpOAuthDTO signUpOAuthDTO,HttpServletResponse httpServletResponse) {
        Map<String, Object> response = new HashMap<>();

        if(signUpOAuthDTO.getOauthProvider().equals(OAuthProvider.GOOGLE)){
            response = userService.registerUserWithGoogle(signUpOAuthDTO.getAccessToken(),signUpOAuthDTO.getRole(),httpServletResponse);
        }else if(signUpOAuthDTO.getOauthProvider().equals(OAuthProvider.FACEBOOK)){
            response = userService.registerUserWithFacebook(signUpOAuthDTO.getAccessToken(),signUpOAuthDTO.getRole(),httpServletResponse);
        }else{
            response.put("error", "Invalid OAuth Provider !!!");
            return ResponseEntity.badRequest().body(response);
        }

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
