package com.demo.Store.service.impl;

import com.demo.Store.enums.UserRole;
import com.demo.Store.model.User;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.request.RegistrationInfoDto;
import com.demo.Store.repository.UserRepository;
import com.demo.Store.service.MessageSourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageSourceService messageSourceService;

    @InjectMocks
    private UserServiceImpl service;

    private static final String USERNAME = "user";

    private static final Long USER_ID = 1L;

    private static final String MESSAGE = "message";

    @Test
    void registerNewUserPersistsUserAndReturnsSuccess() {
        RegistrationInfoDto registrationInfo = new RegistrationInfoDto();
        registrationInfo.setName("John");
        registrationInfo.setSurname("Doe");
        registrationInfo.setUsername(USERNAME);
        registrationInfo.setPassword("plain");
        String encodedPassword = "encoded";
        when(passwordEncoder.encode(registrationInfo.getPassword())).thenReturn(encodedPassword);
        when(messageSourceService.getMessage("store.auth.RegistrationSuccessful")).thenReturn(MESSAGE);

        OperationResultDto result = service.registerNewUser(registrationInfo);

        assertTrue(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(registrationInfo.getName(), savedUser.getName());
        assertEquals(registrationInfo.getSurname(), savedUser.getSurname());
        assertEquals(registrationInfo.getUsername(), savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertEquals(UserRole.USER, savedUser.getRole());
        assertFalse(savedUser.getLocked());
    }

    @Test
    void getUsersByRoleDelegatesToRepository() {
        List<User> users = List.of(new User());
        when(userRepository.getUsersByRole(UserRole.USER)).thenReturn(users);

        List<User> result = service.getUsersByRole(UserRole.USER);

        assertEquals(1, result.size());
        verify(userRepository).getUsersByRole(UserRole.USER);
    }

    @Test
    void updateLastLoginDelegatesToRepository() {
        Instant now = Instant.now();

        service.updateLastLogin(USERNAME, now);

        verify(userRepository).updateLastLogin(USERNAME, now);
    }

    @Test
    void toggleUserAccountLockReturnsInvalidWhenUserMissing() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        when(messageSourceService.getMessage("admin.users.UserDoesNotExist")).thenReturn(MESSAGE);

        OperationResultDto result = service.toggleUserAccountLock(USER_ID);

        assertFalse(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        verify(userRepository, never()).toggleUserAccountLock(USER_ID);
    }

    @Test
    void toggleUserAccountLockUpdatesLockWhenUserExists() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(messageSourceService.getMessage("admin.users.UserAccountModified")).thenReturn(MESSAGE);

        OperationResultDto result = service.toggleUserAccountLock(USER_ID);

        assertTrue(result.getValid());
        assertEquals(MESSAGE, result.getMessage());
        verify(userRepository).toggleUserAccountLock(USER_ID);
    }

    @Test
    void loadUserByUsernameBuildsSpringSecurityUser() {
        String password = "hash";
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(password);
        user.setRole(UserRole.ADMIN);
        user.setLocked(true);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername(USERNAME);

        assertEquals(USERNAME, details.getUsername());
        assertEquals(password, details.getPassword());
        assertFalse(details.isAccountNonLocked());
        assertTrue(details.getAuthorities().stream().anyMatch(auth -> "ADMIN".equals(auth.getAuthority())));
    }

    @Test
    void loadUserByUsernameThrowsWhenUserMissing() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(USERNAME));
    }
}