package com.example.macrominder.authService.dto;

public class SendOtpDTO {

    private Long userId;

    public SendOtpDTO(){

    }

    public SendOtpDTO(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
