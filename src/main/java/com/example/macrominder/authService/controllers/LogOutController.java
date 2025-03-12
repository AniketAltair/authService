package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.dto.SignInDTO;
import com.example.macrominder.authService.services.UserServiceImp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LogOutController {

    private final UserServiceImp userService;

    @Autowired
    public LogOutController(UserServiceImp userService) {
        this.userService = userService;
    }

    // validate access token first
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> response = null;

        // we get userid here
        // from frontend we remove the access token and refresh token
        // from backend we remove stored refresh token againdt the userid

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
