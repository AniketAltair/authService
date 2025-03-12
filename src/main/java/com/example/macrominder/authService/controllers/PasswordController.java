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

        // we get userid
        // we check which email to send mail to
        // primary mail >> google email >> facebook email.
        // generate some unique 6 digit number as otp
        // We store this userid and otp in DB
        // also store the expiry time of this otp in DB along with this, like 15 mins
        // Now we pass this data to the notification service using rabbitmq, which will post this to email via AWS SQS
        // finally we return the success message

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // perform validation of token first
    @PostMapping("confirmotp")
    public ResponseEntity<?> confirmOTP(@RequestBody ConfirmOtpDTO confirmOtpDTO){
        Map<String, Object> response = null;

        // here we get input as userid and otp.
        // we compare this against the otp stored in table
        // return approriate result

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
        // we compare them against each other.
        // if invlaid return error
        // set this hashed new password in db agains userid.
        // return success message

        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

}
