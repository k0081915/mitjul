package com.mitjul.common.error;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception
    ) {
        return handleValidationErrors(exception);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException exception) {
        return handleValidationErrors(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode, errorCode.getMessage()));
    }

    private ResponseEntity<ErrorResponse> handleValidationErrors(BindException exception) {
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        List<ErrorResponse.FieldError> fieldErrors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> new ErrorResponse.FieldError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
            ))
            .toList();

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode, fieldErrors));
    }
}
