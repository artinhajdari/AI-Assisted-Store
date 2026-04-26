package com.demo.Store.util;

import com.demo.Store.model.dto.OperationResultDto;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Objects;

public class ValidationUtils {

    public static OperationResultDto getValidationErrorResult(BindingResult bindingResult) {
        OperationResultDto operationResultDto = new OperationResultDto.Builder().valid(false).build();
        if (Objects.isNull(bindingResult)) {
            return operationResultDto;
        }
        ObjectError objectError = bindingResult.getAllErrors().getFirst();
        operationResultDto.setMessage(objectError.getDefaultMessage());
        return operationResultDto;
    }

}
