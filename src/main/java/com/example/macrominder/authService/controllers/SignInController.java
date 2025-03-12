package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.dto.SignInDTO;
import com.example.macrominder.authService.dto.SignInOAuthDTO;
import com.example.macrominder.authService.dto.SignUpDTO;
import com.example.macrominder.authService.dto.SignUpOAuthDTO;
import com.example.macrominder.authService.enums.OAuthProvider;
import com.example.macrominder.authService.services.UserServiceImp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SignInController {

    private final UserServiceImp userService;

    @Autowired
    public SignInController(UserServiceImp userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDTO signInDTO, HttpServletResponse responseobj) {
        Map<String, Object> response = userService.signInUser(signInDTO,responseobj);

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signinoauth")
    public ResponseEntity<?> signInOAuth(@RequestBody SignInOAuthDTO signInOAuthDTO, HttpServletResponse httpServletResponse) {
        Map<String, Object> response = null;

        if(signInOAuthDTO.getOauthProvider().equals(OAuthProvider.GOOGLE)){
            response = userService.signInUserWithGoogle(signInOAuthDTO.getAccessToken(),httpServletResponse);
        }else if(signInOAuthDTO.getOauthProvider().equals(OAuthProvider.FACEBOOK)){
            response = userService.signInUserWithFacebook(signInOAuthDTO.getAccessToken(),httpServletResponse);
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
