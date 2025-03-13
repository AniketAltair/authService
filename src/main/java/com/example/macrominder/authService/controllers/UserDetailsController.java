package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.invoke.MutableCallSite;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserDetailsController {


    private UserProfileService userProfileService;

    public UserDetailsController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    // perform validation of token first
    @GetMapping("/userDetails/{userId}")
    public ResponseEntity<?> userDetails(@PathVariable Long userId){
        Map<String, Object> response = new HashMap<>();

        response = userProfileService.getUserDetails(userId);

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // perform validation of token first
    @PostMapping("/updateuserDetails/{userId}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable("userId") Long userId,
            @RequestPart(value = "profileImage") MultipartFile profileImage,
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            @RequestParam("googleAccesstoken") String googleAccesstoken,
            @RequestParam("facebookAccesstoken") String facebookAccesstoken
            ){

        Map<String, Object> response = new HashMap<>();

        response = userProfileService.updateUserDetails(
                userId,
                profileImage,
                userName,
                email,
                googleAccesstoken,
                facebookAccesstoken
        );



        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }


}
