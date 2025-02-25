package org.example.libdev.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //@Valid 검증 실패 시 MethodArgumentNotValidException이 발생.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        // 모든 필드 오류 메시지를 리스트로 수집
        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // ErrorResponseDto 생성 (에러가 하나면 단일 메시지 반환, 여러 개면 리스트 형태로 반환)
        ErrorResponseDto errorResponseDto;
        if (errorMessages.size() == 1) {
            errorResponseDto = new ErrorResponseDto(
                    "유효성 검증 실패",
                    errorMessages.get(0) // 리스트가 아니라 단일 메시지로 반환
            );
        } else {
            errorResponseDto = new ErrorResponseDto(
                    "유효성 검증 실패",
                    errorMessages.toString() // 여러 개의 에러 메시지는 리스트 형태 유지
            );
        }

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        ErrorResponseDto errorResponseDto;

        // 타입 에러 (InvalidFormatException) 처리
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;

            String fieldName = invalidFormatException.getPath().stream()
                    .findFirst()
                    .map(ref -> ref.getFieldName())
                    .orElse("unknown");

            String invalidValue = invalidFormatException.getValue().toString();
            String expectedType = invalidFormatException.getTargetType().getSimpleName();

            errorResponseDto = new ErrorResponseDto(

                    "입력한 값 '" + invalidValue + "'은(는) 유효하지 않습니다. '" + fieldName + "' 필드는 " + expectedType
                            + " 형식이어야 합니다."
                    , ex.getMessage()
            );
        } else {
            // JSON 파싱 에러 (타입 에러가 아닌 경우)
            errorResponseDto = new ErrorResponseDto(
                    "JSON 파싱 형식이 올바르지 않습니다.",
                    ex.getMessage()
            );
        }

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }






}
