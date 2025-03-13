package com.example.macrominder.authService.services;

import com.example.macrominder.authService.models.UserInfo;
import com.example.macrominder.authService.models.UserOtp;
import com.example.macrominder.authService.repository.UserOtpRepository;
import com.example.macrominder.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserProfileServiceImp implements UserProfileService {

    private UserRepository userRepository;
    private UserOtpRepository userOtpRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserProfileServiceImp (UserRepository userRepository,
                                  UserOtpRepository userOtpRepository,
                                  PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userOtpRepository = userOtpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, Object> sendOtp(Long userId) {

        Optional<UserInfo> userInfoOptional = userRepository.findById(userId);
        if (userInfoOptional.isEmpty()) {
            return createErrorResponse("User not found");
        }
        UserInfo userInfo = userInfoOptional.get();

        String primaryEmail = getPrimaryEmail(userInfo);
        if (primaryEmail == null) {
            return createErrorResponse("No valid email found for the user");
        }

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
        storeOtp(userInfo, otp, expiryTime);

        // send this otp and userid to another service via rabbit mq.

        return createSuccessResponse("OTP sent successfully");
    }

    @Override
    public Map<String, Object> confirmOtp(Long userId, String otp) {

        Optional<UserInfo> userInfoOptional = userRepository.findById(userId);
        if (userInfoOptional.isEmpty()) {
            return createErrorResponse("User not found");
        }

        UserInfo userInfo = userInfoOptional.get();
        Optional<UserOtp> userOtpOptional = userOtpRepository.findByUserInfoInUserOtp(userInfo);

        if (userOtpOptional.isEmpty()) {
            return createErrorResponse("No OTP found for the user");
        }

        UserOtp userOtp = userOtpOptional.get();
        if (isOtpExpired(userOtp)) {
            return createErrorResponse("OTP has expired");
        }

        if (!userOtp.getOtp().equals(otp)) {
            return createErrorResponse("Invalid OTP");
        }

        return createSuccessResponse("OTP verified successfully");
    }

    @Override
    public Map<String, Object> changePassword(Long userId, String password, String confirmPassword) {

        password = password.trim();
        confirmPassword = confirmPassword.trim();
        if (!password.equals(confirmPassword)) {
            return createErrorResponse("Password and confirm password do not match");
        }

        Optional<UserInfo> userInfoOptional = userRepository.findById(userId);
        if (userInfoOptional.isEmpty()) {
            return createErrorResponse("User not found");
        }

        UserInfo userInfo = userInfoOptional.get();

        String hashedPassword = passwordEncoder.encode(password);
        userInfo.setHashedPassword(hashedPassword);

        userRepository.save(userInfo);

        return createSuccessResponse("Password changed successfully");
    }


    @Override
    public Map<String,Object> updateUserDetails(Long userId,
                                                MultipartFile profileImage,
                                                String userName,
                                                String email,
                                                String googleAccesstoken,
                                                String facebookAccesstoken){

        Map<String, Object> responseData = new HashMap<>();

        Optional<UserInfo> userInfo = userRepository.findById(userId);
        if(userInfo.isEmpty()){
            return createErrorResponse("User not found");
        }

        responseData = setImage(profileImage);

        responseData = setUsername(userName);

        responseData = setEmail(email);

        responseData = setLinkedAccounts(googleAccesstoken,facebookAccesstoken);

        // finally make sure atleast one email from link accounts or email is available for user.
        // finally send this new data, excluding image to notification service to send it to AWS SQS
        // to new email >> google email >> facebook email

        return  responseData;
    }

    public Map<String,Object> getUserDetails(Long userId){
        Map<String, Object> responseData = new HashMap<>();

        // using userId get all user details:
        // - profile pic path (it is part of userdetails table)
        // - username
        // - email
        // - google email
        // - facebook email
        // - role
        return responseData;
    }

    private String getPrimaryEmail(UserInfo userInfo) {
        if (isValidEmail(userInfo.getEmail())) {
            return userInfo.getEmail();
        } else if (isValidEmail(userInfo.getGoogleEmail())) {
            return userInfo.getGoogleEmail();
        } else if (isValidEmail(userInfo.getFacebookEmail())) {
            return userInfo.getFacebookEmail();
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.isBlank();
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    private void storeOtp(UserInfo userInfo, String otp, LocalDateTime expiryTime) {
        Optional<UserOtp> existingOtp = userOtpRepository.findByUserInfoInUserOtp(userInfo);

        if (existingOtp.isPresent()) {
            UserOtp userOtp = existingOtp.get();
            userOtp.setOtp(otp);
            userOtp.setExpiryDate(expiryTime);
            userOtpRepository.save(userOtp);
        } else {
            UserOtp newUserOtp = new UserOtp(userInfo, otp, expiryTime);
            userOtpRepository.save(newUserOtp);
        }
    }

    private boolean isOtpExpired(UserOtp userOtp) {
        return LocalDateTime.now().isAfter(userOtp.getExpiryDate());
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    private Map<String,Object> setImage(MultipartFile profileImage){
        Map<String,Object> response = new HashMap<>();
        // First take the image and upload it to s3 and get back the URL
        // Now delete user profile image path and from s3 both
        // Now add this new image path
        // it is part of userdetails table

        return response;
    }

    private Map<String,Object> setUsername(String username){
        Map<String,Object> response = new HashMap<>();
        // set passed username, no checks here.
        return response;
    }

    private Map<String,Object> setEmail(String email){
        Map<String,Object> response = new HashMap<>();
        // also for email check if it is already present anywhere other than this user in DB.
        // also if check if email is sent , password has been set in db , if no, dont return error as set password for email.
        // also if email sent is null or empty, remove password also, set it to null.
        // Now add or update rest of fields like username and email.
        // also on frontend make sure if email is changed, delete access and refresh tokens
        return response;
    }

    private Map<String,Object> setLinkedAccounts(String googleAccessToken,String facebookAccessToken){
        Map<String,Object> response = new HashMap<>();
        // for linked accounts:
        // get google accesstoken from above
        // validate google accesss token with api
        // if invalid throw error
        // if valid, extract email
        // check across all email,googleemail and facebookemail if already in use
        // except users all email, if match found return Email already in use.
        // if valid,
        // if oauth email empty add this to userinfo.googleemail
        // if oauth email not empty add this to userinfo.googleemail
        // if passed oauth value empty, set db value to null.
        return response;
    }

}
