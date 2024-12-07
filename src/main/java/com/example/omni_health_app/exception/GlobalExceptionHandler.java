package com.example.omni_health_app.exception;

import com.example.omni_health_app.dto.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.omni_health_app.exception.ErrorCode.BAD_REQUEST_WRONG_AUTH_ERROR;
import static com.example.omni_health_app.exception.ErrorCode.INTERNAL_FAILURE_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAuthException.class)
    public ResponseEntity<ResponseWrapper<ErrorResponseData>> handleUserAuthException(UserAuthException ex) {
        ResponseMetadata responseMetadata = ResponseMetadata.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .errors(List.of(ex.getMessage()))
                .errorCode(BAD_REQUEST_WRONG_AUTH_ERROR)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
                        .responseMetadata(responseMetadata)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<ErrorResponseData>> handleGeneralException(Exception ex) {
        ResponseMetadata responseMetadata = ResponseMetadata.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errors(List.of(ex.getMessage()))
                .errorCode(INTERNAL_FAILURE_ERROR)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                .responseMetadata(responseMetadata)
                .build());
    }
}
