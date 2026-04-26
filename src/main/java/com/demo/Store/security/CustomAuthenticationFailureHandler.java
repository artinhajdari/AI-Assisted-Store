package com.demo.Store.security;

import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.service.MessageSourceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final MessageSourceService messageSourceService;

    @Autowired
    public CustomAuthenticationFailureHandler(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        OperationResultDto result = new OperationResultDto();
        result.setValid(false);
        if (exception instanceof LockedException) {
            result.setMessage(messageSourceService.getMessage("store.auth.AccountLocked"));
        } else {
            result.setMessage(messageSourceService.getMessage("store.auth.InvalidCredentials"));
        }
        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
        FlashMap flashMap = new FlashMap();
        flashMap.put("operationResult", result);
        flashMapManager.saveOutputFlashMap(flashMap, request, response);
        super.onAuthenticationFailure(request, response, exception);
    }
}
