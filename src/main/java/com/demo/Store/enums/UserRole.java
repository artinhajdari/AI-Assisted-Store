package com.demo.Store.enums;

public enum UserRole {

    USER,
    ADMIN;

    public UserRole fromString(String value) {
        for (UserRole userRole : values()) {
            if (userRole.name().equals(value)) {
                return userRole;
            }
        }
        return null;
    }

}
