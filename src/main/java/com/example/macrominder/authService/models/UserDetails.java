package com.example.macrominder.authService.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "userdetails")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_info_id", nullable = false, unique = true)
    private UserInfo userInfoInUserDetails;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "signupdatetime")
    private LocalDateTime signUpDateTime;

    @Column(name = "profile_image_url")
    private String profileImageURL;

    public UserDetails(){

    }

    public UserDetails(UserInfo userInfoInUserDetails,
                       boolean isActive,
                       LocalDateTime signUpDateTime,
                       String profileImageURL
                       ) {
        this.userInfoInUserDetails = userInfoInUserDetails;
        this.isActive = isActive;
        this.signUpDateTime = signUpDateTime;
        this.profileImageURL = profileImageURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUserInfoInUserDetails() {
        return userInfoInUserDetails;
    }

    public void setUserInfoInUserDetails(UserInfo userInfoInUserDetails) {
        this.userInfoInUserDetails = userInfoInUserDetails;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getSignUpDateTime() {
        return signUpDateTime;
    }

    public void setSignUpDateTime(LocalDateTime signUpDateTime) {
        this.signUpDateTime = signUpDateTime;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}
