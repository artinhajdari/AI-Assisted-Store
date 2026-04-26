package com.demo.Store.controller.admin;

import com.demo.Store.enums.UserRole;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.mapper.UserMapper;
import com.demo.Store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/list")
    public String manageUsers(Model model) {
        model.addAttribute("users", userMapper.toDtos(userService.getUsersByRole(UserRole.USER)));
        return "admin/user-list";
    }

    @PostMapping("/toggle-lock")
    public String toggleLockOnUser(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String userId = request.getParameter("userId");
        OperationResultDto result = userService.toggleUserAccountLock(NumberUtils.toLong(userId));
        redirectAttributes.addFlashAttribute("operationResult", result);
        return "redirect:/admin/users/list";
    }
}
