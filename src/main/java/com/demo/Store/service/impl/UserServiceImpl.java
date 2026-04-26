package com.demo.Store.service.impl;

import com.demo.Store.enums.UserRole;
import com.demo.Store.model.User;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.request.RegistrationInfoDto;
import com.demo.Store.repository.UserRepository;
import com.demo.Store.service.MessageSourceService;
import com.demo.Store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.Validate;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSourceService messageSourceService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MessageSourceService messageSourceService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSourceService = messageSourceService;
    }

    @Transactional
    @Override
    public OperationResultDto registerNewUser(RegistrationInfoDto registrationInfo) {
        if (userRepository.existsByUsername(registrationInfo.getUsername())) {
            return new OperationResultDto.Builder().valid(false).message(messageSourceService.getMessage("store.auth.UsernameAlreadyInUse")).build();
        }
        User user = new User.Builder().setName(registrationInfo.getName()).setSurname(registrationInfo.getSurname()).setUsername(registrationInfo.getUsername()).setPassword(passwordEncoder.encode(registrationInfo.getPassword())).locked(Boolean.FALSE).setRole(UserRole.USER).createUser();
        userRepository.save(user);
        return new OperationResultDto.Builder().valid(true).message(messageSourceService.getMessage("store.auth.RegistrationSuccessful")).build();
    }

    @Transactional
    @Override
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.getUsersByRole(role);
    }

    @Transactional
    @Override
    public void updateLastLogin(String username, Instant loginDate) {
        userRepository.updateLastLogin(username, loginDate);
    }

    @Transactional
    @Override
    public OperationResultDto toggleUserAccountLock(Long userId) {
        Validate.notNull(userId, "User ID cannot be null at this point.");
        if (!userRepository.existsById(userId)) {
            return new OperationResultDto.Builder().valid(false).message(messageSourceService.getMessage("admin.users.UserDoesNotExist")).build();
        }
        userRepository.toggleUserAccountLock(userId);
        return new OperationResultDto.Builder().valid(true).message(messageSourceService.getMessage("admin.users.UserAccountModified")).build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Boolean accountLocked = user.getLocked();
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .accountLocked(Objects.nonNull(accountLocked) && accountLocked)
                .build();
    }
}
