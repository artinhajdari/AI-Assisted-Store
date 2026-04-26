package com.demo.Store.util;

import com.demo.Store.model.dto.OperationResultDto;

public class TestUtils {

    public static final String OPERATION_RESULT = "operationResult";

    public static OperationResultDto getOperationResult(boolean valid, String message) {
        return new OperationResultDto.Builder().valid(valid).message(message).build();
    }

}
