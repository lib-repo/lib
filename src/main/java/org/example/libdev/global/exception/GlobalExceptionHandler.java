package org.example.libdev.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validated 시 바인딩 에러가 존재할 때 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.info("Handle MethodArgumentNotValidException", e);

        final int status = HttpStatus.BAD_REQUEST.value();

        final ErrorResponse response = ErrorResponse.of(status, ErrorCode.INVALID_INPUT_VALUE,
                e.getBindingResult());

        return new ResponseEntity<>(response, HttpStatus.valueOf(status));
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        log.info("Handle BusinessException", e);

        final ErrorCode errorCode = e.getErrorCode();
        final int status = HttpStatus.BAD_GATEWAY.value();

        final ErrorResponse errorResponse = ErrorResponse.of(status, errorCode);

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(status));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Handle Exception", e);

        final int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

        final ErrorResponse response = ErrorResponse.of(status, ErrorCode.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
