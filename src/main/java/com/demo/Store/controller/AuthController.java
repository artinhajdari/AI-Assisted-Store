package com.demo.Store.controller;

import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.request.RegistrationInfoDto;
import com.demo.Store.service.MessageSourceService;
import com.demo.Store.service.UserService;
import com.demo.Store.util.ValidationConstants;
import com.demo.Store.util.ValidationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    private final UserService userService;
    private final MessageSourceService messageSourceService;

    @Autowired
    public AuthController(UserService userService, MessageSourceService messageSourceService) {
        this.userService = userService;
        this.messageSourceService = messageSourceService;
    }

    @ModelAttribute("validation")
    public Map<String, Object> validation() {
        Map<String, Object> map = new HashMap<>();
        map.put("nameMinLength", ValidationConstants.NAME_MIN_LENGTH);
        map.put("nameMaxLength", ValidationConstants.NAME_MAX_LENGTH);
        map.put("passwordMinLength", ValidationConstants.PASSWORD_MIN_LENGTH);
        map.put("passwordMaxLength", ValidationConstants.PASSWORD_MAX_LENGTH);
        String minLengthMessage = messageSourceService.getMessage("store.validation.MinLength");
        String maxLengthMessage = messageSourceService.getMessage("store.validation.MaxLength");
        map.put("nameMinLengthMessage", String.format(minLengthMessage, ValidationConstants.NAME_MIN_LENGTH));
        map.put("nameMaxLengthMessage", String.format(maxLengthMessage, ValidationConstants.NAME_MAX_LENGTH));
        map.put("passwordMinLengthMessage", String.format(minLengthMessage, ValidationConstants.PASSWORD_MIN_LENGTH));
        map.put("passwordMaxLengthMessage", String.format(maxLengthMessage, ValidationConstants.PASSWORD_MAX_LENGTH));
        return map;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "loggedOut", required = false) String loggedOut, Model model) {
        if (Boolean.parseBoolean(loggedOut)) {
            model.addAttribute("operationResult", new OperationResultDto.Builder().valid(true).message(messageSourceService.getMessage("store.auth.LoggedOut")).build());
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerGet() {
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute RegistrationInfoDto registrationInfo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("operationResult", ValidationUtils.getValidationErrorResult(bindingResult));
            return "redirect:/register";
        }
        OperationResultDto operationResultDto = userService.registerNewUser(registrationInfo);
        redirectAttributes.addFlashAttribute("operationResult", operationResultDto);
        return "redirect:/login";
    }

}
