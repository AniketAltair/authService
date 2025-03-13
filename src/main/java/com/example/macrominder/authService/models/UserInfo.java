package com.example.macrominder.authService.models;

import jakarta.persistence.*;

@Entity
@Table(name = "userinfo")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name="hashed_password")
    private String hashedPassword;

    @Column(name="username")
    private String userName;

    @Column(name="googleemail")
    private String googleEmail;

    @Column(name="facebookemail")
    private String facebookEmail;

    @OneToOne(mappedBy = "userInfoInUserRole", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserRole role;

    @OneToOne(mappedBy = "userInfoInUserRefreshToken", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserRefreshToken refreshToken;

    @OneToOne(mappedBy = "userInfoInUserDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserDetails signUpDateTime;

    @OneToOne(mappedBy = "userInfoInUserOtp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserOtp otp;

    // Constructors
    public UserInfo() {}

    public UserInfo(String email, String hashedPassword, String userName, String googleEmail, String facebookEmail) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.userName = userName;
        this.googleEmail = googleEmail;
        this.facebookEmail = facebookEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserRefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(UserRefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDetails getSignUpDateTime() {
        return signUpDateTime;
    }

    public void setSignUpDateTime(UserDetails signUpDateTime) {
        this.signUpDateTime = signUpDateTime;
    }
}
