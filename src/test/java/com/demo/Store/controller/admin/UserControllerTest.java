package com.demo.Store.controller.admin;

import com.demo.Store.enums.UserRole;
import com.demo.Store.model.User;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.UserDto;
import com.demo.Store.model.dto.mapper.UserMapper;
import com.demo.Store.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController controller;

    @Test
    void manageUsersReturnsUserListView() {
        List<User> users = List.of(new User());
        List<UserDto> userDtos = List.of(new UserDto());
        when(userService.getUsersByRole(UserRole.USER)).thenReturn(users);
        when(userMapper.toDtos(users)).thenReturn(userDtos);

        String result = controller.manageUsers(model);

        assertEquals("admin/user-list", result);
        verify(model).addAttribute("users", userDtos);
    }

    @Test
    void toggleLockOnUserCallsServiceAndRedirects() {
        when(request.getParameter("userId")).thenReturn("9");
        OperationResultDto operationResult = new OperationResultDto.Builder().valid(true).message("ok").build();
        when(userService.toggleUserAccountLock(9L)).thenReturn(operationResult);

        String result = controller.toggleLockOnUser(request, redirectAttributes);

        assertEquals("redirect:/admin/users/list", result);
        verify(redirectAttributes).addFlashAttribute("operationResult", operationResult);
    }
}