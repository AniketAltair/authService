package com.example.macrominder.authService.services;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserProfileService {
    public Map<String,Object> sendOtp(Long userId);
    public Map<String,Object> confirmOtp(Long userId,String Otp);
    public Map<String,Object> changePassword(Long userId,String password,String confirmPassword);
    public Map<String,Object> updateUserDetails(Long userId,
                                                MultipartFile profileImage,
                                                String userName,
                                                String email,
                                                String googleAccesstoken,
                                                String facebookAccesstoken);
    public Map<String,Object> getUserDetails(Long userId);
}
