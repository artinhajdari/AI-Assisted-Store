package com.demo.Store.repository;

import com.demo.Store.enums.UserRole;
import com.demo.Store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.username = :username")
    void updateLastLogin(String username, Instant lastLogin);

    @Query("SELECT u from User u where u.role = :role")
    List<User> getUsersByRole(UserRole role);

    @Modifying
    @Query("UPDATE User u SET u.locked = CASE WHEN u.locked = true THEN false ELSE true END WHERE u.id = :userId")
    void toggleUserAccountLock(Long userId);
}


