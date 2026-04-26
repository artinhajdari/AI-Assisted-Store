package com.demo.Store.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RegistrationInfoDto extends InfoDto {

    @NotBlank(message = "{store.auth.NameRequired}")
    private String name;
    @NotBlank(message = "{store.auth.SurnameRequired}")
    private String surname;
    @NotBlank(message = "{store.auth.EmailRequired}")
    private String username;
    @NotBlank(message = "{store.auth.PasswordRequired}")
    private String password;
    @NotBlank(message = "{store.auth.PasswordConfirmationRequired}")
    private String passwordConfirmation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }
}
