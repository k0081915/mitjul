package com.mitjul.common.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String code,
    String message,
    List<FieldError> errors
) {

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
            LocalDateTime.now(),
            errorCode.getStatus().value(),
            errorCode.name(),
            message,
            List.of()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
        return new ErrorResponse(
            LocalDateTime.now(),
            errorCode.getStatus().value(),
            errorCode.name(),
            errorCode.getMessage(),
            errors
        );
    }

    public record FieldError(
        String field,
        String message
    ) {
    }
}
