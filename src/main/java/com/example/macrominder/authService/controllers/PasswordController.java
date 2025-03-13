package com.example.macrominder.authService.controllers;

import com.example.macrominder.authService.dto.ChangePasswordDTO;
import com.example.macrominder.authService.dto.ConfirmOtpDTO;
import com.example.macrominder.authService.dto.SendOtpDTO;
import com.example.macrominder.authService.services.UserProfileService;
import com.example.macrominder.authService.services.UserProfileServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class PasswordController {

    private UserProfileService userProfileService;

    public PasswordController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }

    // perform validation of token first
    @PostMapping("/sendotp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDTO sendOtpDTO){
        Map<String, Object> response = null;

        response = userProfileService.sendOtp(sendOtpDTO.getUserId());

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // perform validation of token first
    @PostMapping("confirmotp")
    public ResponseEntity<?> confirmOTP(@RequestBody ConfirmOtpDTO confirmOtpDTO){
        Map<String, Object> response = null;

        response = userProfileService.confirmOtp(confirmOtpDTO.getUserId(),confirmOtpDTO.getUserOtp());

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // perform validation of token first
    @PostMapping("/changepassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        Map<String, Object> response = null;

        // we get the userid and new password and confirm password
        response = userProfileService.changePassword(
                changePasswordDTO.getUserId(),
                changePasswordDTO.getPassword(),
                changePasswordDTO.getConfirmPassword());


        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

}
