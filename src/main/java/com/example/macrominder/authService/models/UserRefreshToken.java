package com.example.macrominder.authService.models;

import jakarta.persistence.*;

@Entity
@Table(name="userrefreshtoken")
public class UserRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_info_id", nullable = false, unique = true)
    private UserInfo userInfoInUserRefreshToken;

    @Column(name = "user_refresh_token")
    private String userRefreshToken;

    public UserRefreshToken(){

    }

    public UserRefreshToken(UserInfo userInfoInUserRefreshToken, String userRefreshToken) {
        this.userInfoInUserRefreshToken = userInfoInUserRefreshToken;
        this.userRefreshToken = userRefreshToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUserInfoInUserRefreshToken() {
        return userInfoInUserRefreshToken;
    }

    public void setUserInfoInUserRefreshToken(UserInfo userInfoInUserRefreshToken) {
        this.userInfoInUserRefreshToken = userInfoInUserRefreshToken;
    }

    public String getUserRefreshToken() {
        return userRefreshToken;
    }

    public void setUserRefreshToken(String userRefreshToken) {
        this.userRefreshToken = userRefreshToken;
    }
}
