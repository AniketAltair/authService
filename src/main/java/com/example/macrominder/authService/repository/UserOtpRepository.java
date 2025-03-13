package com.example.macrominder.authService.repository;

import com.example.macrominder.authService.models.UserInfo;
import com.example.macrominder.authService.models.UserOtp;
import com.example.macrominder.authService.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    Optional<UserOtp> findByUserInfoInUserOtp(UserInfo userInfo);
}
