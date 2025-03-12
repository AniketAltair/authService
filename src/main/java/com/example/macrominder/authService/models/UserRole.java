package com.example.macrominder.authService.models;

import jakarta.persistence.*;

@Entity
@Table(name = "userrole")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_info_id", nullable = false, unique = true)
    private UserInfo userInfoInUserRole;

    @Column(nullable = false)
    private String userRole;

    // Constructors
    public UserRole() {}

    public UserRole(UserInfo userInfoInUserRole, String userRole) {
        this.userInfoInUserRole = userInfoInUserRole;
        this.userRole = userRole;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getUserInfoInUserRole() {
        return userInfoInUserRole;
    }

    public void setUserInfoInUserRole(UserInfo userInfoInUserRole) {
        this.userInfoInUserRole = userInfoInUserRole;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
