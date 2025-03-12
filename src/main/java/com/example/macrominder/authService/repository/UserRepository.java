package com.example.macrominder.authService.repository;

import com.example.macrominder.authService.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
    Optional<UserInfo> findByGoogleEmail(String email);
    Optional<UserInfo> findByFacebookEmail(String email);

}
