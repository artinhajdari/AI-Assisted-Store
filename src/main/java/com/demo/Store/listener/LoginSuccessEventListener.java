package com.demo.Store.listener;

import com.demo.Store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class LoginSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserService userService;

    @Autowired
    public LoginSuccessEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            userService.updateLastLogin(username, Instant.now());
        }
    }
}