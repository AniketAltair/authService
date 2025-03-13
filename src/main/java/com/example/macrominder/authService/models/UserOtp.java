package com.example.macrominder.authService.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "userotp")
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_info_id", nullable = false, unique = true)
    private UserInfo userInfoInUserOtp;

    @Column(name = "otp")
    private String otp;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public UserOtp(){

    }

    public UserOtp(UserInfo userInfoInUserOtp,
                   String otp,
                   LocalDateTime expiryDate) {
        this.userInfoInUserOtp = userInfoInUserOtp;
        this.otp = otp;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUserInfoInUserOtp() {
        return userInfoInUserOtp;
    }

    public void setUserInfoInUserOtp(UserInfo userInfoInUserOtp) {
        this.userInfoInUserOtp = userInfoInUserOtp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
