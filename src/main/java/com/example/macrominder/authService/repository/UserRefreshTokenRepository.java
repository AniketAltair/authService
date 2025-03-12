package com.example.macrominder.authService.repository;

import com.example.macrominder.authService.models.UserInfo;
import com.example.macrominder.authService.models.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken,Long> {
    Optional<UserRefreshToken> findByUserInfoInUserRefreshToken(UserInfo userInfoInUserRefreshToken);
}
