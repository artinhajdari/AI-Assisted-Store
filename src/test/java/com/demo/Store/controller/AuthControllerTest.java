package com.demo.Store.controller;

import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.request.RegistrationInfoDto;
import com.demo.Store.service.MessageSourceService;
import com.demo.Store.service.UserService;
import com.demo.Store.util.ValidationConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

import static com.demo.Store.util.TestUtils.OPERATION_RESULT;
import static com.demo.Store.util.TestUtils.getOperationResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController controller;

    @Test
    void validationBuildsValidationMap() {
        String testMinLength = "testMinLength %s";
        String testMaxLength = "testMaxLength %s";
        when(messageSourceService.getMessage("store.validation.MinLength")).thenReturn(testMinLength);
        when(messageSourceService.getMessage("store.validation.MaxLength")).thenReturn(testMaxLength);

        Map<String, Object> validationMap = controller.validation();

        assertEquals(String.format(testMinLength, ValidationConstants.NAME_MIN_LENGTH), validationMap.get("nameMinLengthMessage"));
        assertEquals(String.format(testMaxLength, ValidationConstants.NAME_MAX_LENGTH), validationMap.get("nameMaxLengthMessage"));
    }

    @Test
    void loginPageAddsFlashModelWhenLoggedOut() {
        String loggedOut = "Logged Out";
        when(messageSourceService.getMessage("store.auth.LoggedOut")).thenReturn(loggedOut);

        String viewName = controller.loginPage("true", model);

        assertEquals("login", viewName);
        verify(model).addAttribute(OPERATION_RESULT, getOperationResult(true, loggedOut));
    }

    @Test
    void registerGetReturnsRegistrationPage() {
        String result = controller.registerGet();
        assertEquals("register", result);
    }

    @Test
    void registerPostReturnsRegisterRedirectOnValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String validationFailed = "Validation failed";
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("registrationInfo", "Validation failed")));
        OperationResultDto operationResult = getOperationResult(false, validationFailed);

        String result = controller.registerPost(new RegistrationInfoDto(), bindingResult, redirectAttributes);

        assertEquals("redirect:/register", result);
        verify(redirectAttributes).addFlashAttribute(OPERATION_RESULT, operationResult);
        verify(userService, never()).registerNewUser(any());
    }

    @Test
    void registerPostReturnsLoginRedirectWhenServiceSucceeds() {
        when(bindingResult.hasErrors()).thenReturn(false);
        OperationResultDto serviceResult = getOperationResult(true, "User registered");
        when(userService.registerNewUser(any())).thenReturn(serviceResult);

        String result = controller.registerPost(new RegistrationInfoDto(), bindingResult, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("operationResult", serviceResult);
        verify(userService).registerNewUser(any(RegistrationInfoDto.class));
    }
}
