package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.dto.SignInDTO;
import com.example.macrominder.authService.services.UserServiceImp;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logout(@PathVariable Long userId) {
        Map<String, Object> response = null;

        response = userService.logOut(userId);

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
