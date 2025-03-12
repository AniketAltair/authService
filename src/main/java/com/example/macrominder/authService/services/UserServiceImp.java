package com.example.macrominder.authService.services;

import com.example.macrominder.authService.dto.SignInDTO;
import com.example.macrominder.authService.enums.AuthType;
import com.example.macrominder.authService.helper.JwtUtil;
import com.example.macrominder.authService.dto.SignUpDTO;
import com.example.macrominder.authService.models.UserDetails;
import com.example.macrominder.authService.models.UserInfo;
import com.example.macrominder.authService.models.UserRefreshToken;
import com.example.macrominder.authService.models.UserRole;
import com.example.macrominder.authService.repository.UserDetailsRepository;
import com.example.macrominder.authService.repository.UserRefreshTokenRepository;
import com.example.macrominder.authService.repository.UserRepository;
import com.example.macrominder.authService.repository.UserRoleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Value("${oauth.facebook.id}")
    private String facebookAppId;

    @Value("${oauth.facebook.secret}")
    private String facebookAppSecret;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImp(UserRepository userRepository,
                          UserRoleRepository userRoleRepository,
                          UserRefreshTokenRepository userRefreshTokenRepository,
                          UserDetailsRepository userDetailsRepository,
                          JwtUtil jwtUtil
                          ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public Map<String, Object> registerUser(SignUpDTO signUpDTO, HttpServletResponse response) {
        Map<String, Object> responseData = new HashMap<>();

        if (isEmailAlreadyInUse(signUpDTO.getEmail())) {
            responseData.put("error", "Email Already in Use");
            return responseData;
        }

        UserInfo newUser = createUser(signUpDTO);
        assignUserRole(newUser, signUpDTO.getRole());

        String accessToken = jwtUtil.generateAccessToken(newUser.getId(), newUser.getEmail(), AuthType.EMAIL.toString());
        String refreshToken = jwtUtil.generateRefreshToken(newUser.getId(), newUser.getEmail(), AuthType.EMAIL.toString());

        storeRefreshToken(newUser, refreshToken);
        setRefreshTokenCookie(response, refreshToken);
        setUserDetails(newUser);

        return buildResponseData(newUser, accessToken, signUpDTO.getRole(),AuthType.EMAIL.toString());
    }

    @Override
    public Map<String, Object> signInUser(SignInDTO signInDTO,HttpServletResponse response) {
        Map<String, Object> responseData = new HashMap<>();

        Optional<UserInfo> user = findEmail(signInDTO.getEmail());

        if (user.isEmpty()) {
            responseData.put("error", "Email not found !!!");
            return responseData;
        }

        if(!checkPassword(signInDTO.getPassword(), user.get().getHashedPassword())){
            responseData.put("error", "Invalid Password !!!" );
            return responseData;
        }

        String accessToken = jwtUtil.generateAccessToken(user.get().getId(), user.get().getEmail(),AuthType.EMAIL.toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getId(), user.get().getEmail(),AuthType.EMAIL.toString());

        updateRefreshToken(user.get(), refreshToken);
        setRefreshTokenCookie(response, refreshToken);

        return buildResponseData(user.get(), accessToken,user.get().getRole().getUserRole(),AuthType.EMAIL.toString());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    @Override
    public Map<String,Object> registerUserWithGoogle(String accesstoken,
                                                     String role,
                                                     HttpServletResponse httpServletResponse){

        Map<String, Object> responseData = new HashMap<>();

        responseData = validateGoogleToken(accesstoken);

        if(responseData.containsKey("error")){
            return responseData;
        }

        String email = (String) responseData.get("email");

        if(findEmail(email).isPresent() || findGoogleEmail(email).isPresent() || findFacebookEmail(email).isPresent()){
            responseData.put("error","Account already Exists !!!");
            return responseData;
        }

        UserInfo newUser = createGoogleUser(email);
        assignUserRole(newUser, role);

        String accessToken = jwtUtil.generateAccessToken(newUser.getId(), newUser.getGoogleEmail(), AuthType.GOOGLEEMAIL.toString());
        String refreshToken = jwtUtil.generateRefreshToken(newUser.getId(), newUser.getGoogleEmail(), AuthType.GOOGLEEMAIL.toString());

        storeRefreshToken(newUser, refreshToken);
        setRefreshTokenCookie(httpServletResponse, refreshToken);
        setUserDetails(newUser);

        return buildResponseData(newUser, accessToken,role,AuthType.GOOGLEEMAIL.toString());

    }

    @Override
    public Map<String,Object> signInUserWithGoogle(String accesstoken,
                                                     HttpServletResponse httpServletResponse){

        Map<String, Object> responseData = new HashMap<>();

        responseData = validateGoogleToken(accesstoken);

        if(responseData.containsKey("error")){
            return responseData;
        }

        String email = (String) responseData.get("email");

        if(!findGoogleEmail(email).isPresent()){
            responseData.put("error","Account does not Exist !!!");
            return responseData;
        }

        Optional<UserInfo> user = findGoogleEmail(email);

        if (user.isEmpty()) {
            responseData.put("error", "Email not found !!!");
            return responseData;
        }

        String accessToken = jwtUtil.generateAccessToken(user.get().getId(), user.get().getGoogleEmail(),AuthType.GOOGLEEMAIL.toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getId(), user.get().getGoogleEmail(),AuthType.GOOGLEEMAIL.toString());

        updateRefreshToken(user.get(), refreshToken);
        setRefreshTokenCookie(httpServletResponse, refreshToken);

        return buildResponseData(user.get(), accessToken,user.get().getRole().getUserRole(),AuthType.GOOGLEEMAIL.toString());
    }

    @Override
    public Map<String,Object> registerUserWithFacebook(String accesstoken,
                                                       String role,
                                                       HttpServletResponse httpServletResponse){

        Map<String, Object> responseData = new HashMap<>();

        responseData = validateFacebookToken(accesstoken);

        if(responseData.containsKey("error")){
            return responseData;
        }

        String email = (String) responseData.get("email");

        if(findEmail(email).isPresent() || findGoogleEmail(email).isPresent() || findFacebookEmail(email).isPresent()){
            responseData.put("error","Account already Exists !!!");
            return responseData;
        }

        UserInfo newUser = createFacebookUser(email);
        assignUserRole(newUser, role);

        String accessToken = jwtUtil.generateAccessToken(newUser.getId(), newUser.getFacebookEmail(), AuthType.FACEBOOKEMAIL.toString());
        String refreshToken = jwtUtil.generateRefreshToken(newUser.getId(), newUser.getFacebookEmail(), AuthType.FACEBOOKEMAIL.toString());

        storeRefreshToken(newUser, refreshToken);
        setRefreshTokenCookie(httpServletResponse, refreshToken);
        setUserDetails(newUser);

        return buildResponseData(newUser, accessToken,role,AuthType.FACEBOOKEMAIL.toString());
    }

    @Override
    public Map<String,Object> signInUserWithFacebook(String accesstoken,
                                                   HttpServletResponse httpServletResponse){

        Map<String, Object> responseData = new HashMap<>();

        responseData = validateFacebookToken(accesstoken);

        if(responseData.containsKey("error")){
            return responseData;
        }

        String email = (String) responseData.get("email");

        if(!findFacebookEmail(email).isPresent()){
            responseData.put("error","Account does not Exist !!!");
            return responseData;
        }

        Optional<UserInfo> user = findFacebookEmail(email);

        if (user.isEmpty()) {
            responseData.put("error", "Email not found !!!");
            return responseData;
        }

        String accessToken = jwtUtil.generateAccessToken(user.get().getId(), user.get().getFacebookEmail(),AuthType.FACEBOOKEMAIL.toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getId(), user.get().getFacebookEmail(),AuthType.FACEBOOKEMAIL.toString());

        updateRefreshToken(user.get(), refreshToken);
        setRefreshTokenCookie(httpServletResponse, refreshToken);

        return buildResponseData(user.get(), accessToken,user.get().getRole().getUserRole(),AuthType.FACEBOOKEMAIL.toString());
    }

    @Override
    public Optional<UserInfo> findEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserInfo> findGoogleEmail(String email) {
        return userRepository.findByGoogleEmail(email);
    }

    @Override
    public Optional<UserInfo> findFacebookEmail(String email) {
        return userRepository.findByFacebookEmail(email);
    }

    @Override
    public Map<String,Object> updateTokens(UserInfo userInfo,HttpServletResponse response,String authType){

        // also generate tokens by googleEmail or facebookEmail in case of oauth in case of Oauth

        String accessToken = "";
        String refreshToken = "";

        if(authType.equals(AuthType.GOOGLEEMAIL.toString())){
            accessToken = jwtUtil.generateAccessToken(userInfo.getId(), userInfo.getGoogleEmail(),authType);
            refreshToken = jwtUtil.generateRefreshToken(userInfo.getId(), userInfo.getGoogleEmail(),authType);
        }else if(authType.equals(AuthType.FACEBOOKEMAIL.toString())){
            accessToken = jwtUtil.generateAccessToken(userInfo.getId(), userInfo.getFacebookEmail(),authType);
            refreshToken = jwtUtil.generateRefreshToken(userInfo.getId(), userInfo.getFacebookEmail(),authType);
        }else{
            accessToken = jwtUtil.generateAccessToken(userInfo.getId(), userInfo.getEmail(),authType);
            refreshToken = jwtUtil.generateRefreshToken(userInfo.getId(), userInfo.getEmail(),authType);
        }

        updateRefreshToken(userInfo, refreshToken);
        setRefreshTokenCookie(response, refreshToken);

        return buildResponseData(userInfo,accessToken,userInfo.getRole().getUserRole(),authType);
    }

    private boolean isEmailAlreadyInUse(String email) {
        boolean emailExists = userRepository.findByEmail(email).isPresent() ||
                userRepository.findByGoogleEmail(email).isPresent() ||
                userRepository.findByFacebookEmail(email).isPresent();
        return emailExists;
    }

    private UserInfo createUser(SignUpDTO signUpDTO) {
        String hashedPassword = passwordEncoder.encode(signUpDTO.getPassword());
        UserInfo newUser = new UserInfo(signUpDTO.getEmail(), hashedPassword, null, null, null);
        return userRepository.save(newUser);
    }

    public UserInfo createGoogleUser(String email){
        UserInfo newUser = new UserInfo(null,null,null,email,null);
        return userRepository.save(newUser);
    }

    public UserInfo createFacebookUser(String email){
        UserInfo newUser = new UserInfo(null,null,null,null,email);
        return userRepository.save(newUser);
    }

    private void assignUserRole(UserInfo user, String role) {
        UserRole userRole = new UserRole(user, role);
        userRoleRepository.save(userRole);
    }

    private void storeRefreshToken(UserInfo user, String refreshToken) {
        UserRefreshToken userRefreshToken = new UserRefreshToken(user, refreshToken);
        userRefreshTokenRepository.save(userRefreshToken);
    }

    private void updateRefreshToken(UserInfo user, String refreshToken){
        Optional<UserRefreshToken> existingToken = userRefreshTokenRepository.findByUserInfoInUserRefreshToken(user);

        if (existingToken.isPresent()) {
            UserRefreshToken userRefreshToken = existingToken.get();
            userRefreshToken.setUserRefreshToken(refreshToken);
            userRefreshTokenRepository.save(userRefreshToken);
        } else {
            throw new IllegalStateException("No refresh token found for the user. Cannot update.");
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/auth/refresh");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        response.addCookie(refreshTokenCookie);
    }

    private void setUserDetails(UserInfo user){
        UserDetails userDetails = new UserDetails(user,true, LocalDateTime.now(),null);
        userDetailsRepository.save(userDetails);
    }

    private Map<String, Object> buildResponseData(UserInfo user, String accessToken, String role,String authType) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);
        responseData.put("tokenType", "Bearer");
        responseData.put("expiresIn", 36000);
        responseData.put("userId", user.getId());
        if(authType.equals(AuthType.GOOGLEEMAIL.toString())){
            responseData.put("email", user.getGoogleEmail());
        }else if(authType.equals(AuthType.FACEBOOKEMAIL.toString())){
            responseData.put("email", user.getFacebookEmail());
        }else{
            responseData.put("email", user.getEmail());
        }
        responseData.put("role", role);
        return responseData;
    }

    public Map<String, Object> validateGoogleToken(String token) {
        Map<String, Object> responseData = new HashMap<>();

        String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> googleUserData = response.getBody();
                String email = (String) googleUserData.get("email");
                String userId = (String) googleUserData.get("sub"); // Google's unique user ID
                String issuer = "accounts.google.com"; // OAuth tokens don't return issuer
                boolean emailVerified = (Boolean) googleUserData.get("email_verified");

                if (!emailVerified) {
                    responseData.put("error", "Email not verified");
                    return responseData;
                }

                responseData.put("email", email);
                responseData.put("googleUserId", userId);
                responseData.put("message", "Google access token is valid");
            } else {
                responseData.put("error", "Invalid access token");
            }

        } catch (Exception e) {
            responseData.put("error", "Failed to validate access token: " + e.getMessage());
        }

        return responseData;
    }


    public Map<String, Object> validateFacebookToken(String userAccessToken) {
        Map<String, Object> responseData = new HashMap<>();

        // Your Facebook App credentials
        String appId = facebookAppId;
        String appSecret = facebookAppSecret;

        // Step 1: Get the App Access Token
        String appAccessTokenUrl = "https://graph.facebook.com/oauth/access_token" +
                "?client_id=" + appId +
                "&client_secret=" + appSecret +
                "&grant_type=client_credentials";

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> appTokenResponse = restTemplate.getForEntity(appAccessTokenUrl, Map.class);
            if (!appTokenResponse.getStatusCode().equals(HttpStatus.OK) || appTokenResponse.getBody() == null) {
                responseData.put("error", "Failed to retrieve Facebook App Access Token");
                return responseData;
            }

            String appAccessToken = (String) appTokenResponse.getBody().get("access_token");

            // Step 2: Validate the User's Access Token
            String debugTokenUrl = "https://graph.facebook.com/debug_token" +
                    "?input_token=" + userAccessToken +
                    "&access_token=" + appAccessToken;

            ResponseEntity<Map> debugTokenResponse = restTemplate.getForEntity(debugTokenUrl, Map.class);
            if (!debugTokenResponse.getStatusCode().equals(HttpStatus.OK) || debugTokenResponse.getBody() == null) {
                responseData.put("error", "Invalid Facebook Access Token");
                return responseData;
            }

            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) debugTokenResponse.getBody().get("data"));

            if (data == null || !(Boolean) data.get("is_valid")) {
                responseData.put("error", "Invalid or expired Facebook Access Token");
                return responseData;
            }

            // Step 3: Get User Info (including email)
            String userId = (String) data.get("user_id");
            String userInfoUrl = "https://graph.facebook.com/" + userId +
                    "?fields=id,name,email" +
                    "&access_token=" + userAccessToken;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK) || userInfoResponse.getBody() == null) {
                responseData.put("error", "Failed to retrieve Facebook user info");
                return responseData;
            }

            Map<String, Object> userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");

            if (email == null || email.isEmpty()) {
                responseData.put("error", "No email associated with this account");
                return responseData;
            }

            responseData.put("email", email);
            responseData.put("facebookUserId", userId);
            responseData.put("name", userInfo.get("name"));
            responseData.put("message", "Facebook access token is valid");

        } catch (Exception e) {
            responseData.put("error", "Error validating Facebook token: " + e.getMessage());
        }

        return responseData;
    }

}