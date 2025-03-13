package com.example.macrominder.authService.dto;

public class ConfirmOtpDTO {

    private Long userId;
    private String userOtp;

    public ConfirmOtpDTO(){

    }

    public ConfirmOtpDTO(String userOtp) {
        this.userOtp = userOtp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserOtp() {
        return userOtp;
    }

    public void setUserOtp(String userOtp) {
        this.userOtp = userOtp;
    }
}
