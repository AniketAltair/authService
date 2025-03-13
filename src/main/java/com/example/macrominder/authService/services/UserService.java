package com.example.macrominder.authService.services;

import com.example.macrominder.authService.dto.SignInDTO;
import com.example.macrominder.authService.dto.SignUpDTO;
import com.example.macrominder.authService.models.UserInfo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    public Map<String, Object> registerUser(SignUpDTO signUpDTO,HttpServletResponse response);
    public Map<String,Object> signInUser(SignInDTO signInDTO,HttpServletResponse response);
    public Optional<UserInfo> findEmail(String email);
    public Optional<UserInfo> findGoogleEmail(String email);
    public Optional<UserInfo> findFacebookEmail(String email);
    public Map<String,Object> updateTokens(UserInfo userInfo,HttpServletResponse responseobj,String authType);
    public Map<String,Object> registerUserWithGoogle(String accesstoken,String role,HttpServletResponse httpServletResponse);
    public Map<String,Object> registerUserWithFacebook(String accesstoken,String role,HttpServletResponse httpServletResponse);
    public Map<String,Object> signInUserWithGoogle(String accesstoken, HttpServletResponse httpServletResponse);
    public Map<String,Object> signInUserWithFacebook(String accesstoken, HttpServletResponse httpServletResponse);
    public Map<String,Object> logOut(Long userId);
}
