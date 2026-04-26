package com.demo.Store.service;

import com.demo.Store.enums.UserRole;
import com.demo.Store.model.User;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.request.RegistrationInfoDto;

import java.time.Instant;
import java.util.List;

public interface UserService {

    /**
     * Registers a new user account from the provided registration data.
     *
     * @param registrationInfo registration payload with identity and credentials
     * @return operation result describing whether registration succeeded
     */
    OperationResultDto registerNewUser(RegistrationInfoDto registrationInfo);

    /**
     * Fetches users that belong to a specific role.
     *
     * @param role role used to filter users
     * @return users that belong to the provided role
     */
    List<User> getUsersByRole(UserRole role);

    /**
     * Updates last login timestamp for the user identified by username.
     *
     * @param username unique username of the account to update
     * @param loginDate timestamp to store as last login
     */
    void updateLastLogin(String username, Instant loginDate);

    /**
     * Toggles the lock state of a user account.
     *
     * @param userId identifier of the account to lock or unlock
     * @return operation result describing whether the lock update succeeded
     */
    OperationResultDto toggleUserAccountLock(Long userId);
}
