package com.demo.Store.advices;
import com.demo.Store.service.MessageSourceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSourceService messageSourceService;

    @Autowired
    public GlobalExceptionHandler(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    @ExceptionHandler(value = Exception.class, produces = "application/json")
    public ResponseEntity<Object> defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
        String acceptHeader = request.getHeader("Accept");
        if (Objects.nonNull(acceptHeader) && !acceptHeader.contains("application/json")) {
            throw e;
        }
        Map<String, String> errorMap = Collections.singletonMap("errorMessage", messageSourceService.getMessage("error.InternalServerError"));
        return ResponseEntity.internalServerError().body(errorMap);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
