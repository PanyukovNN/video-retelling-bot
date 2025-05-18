package ru.panyukovnn.videoretellingbot.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.panyukovnn.videoretellingbot.dto.common.CommonResponse;
import ru.panyukovnn.videoretellingbot.dto.common.ValidationError;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ConveyorExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        List<ValidationError> validationErrors = bindingResult.getFieldErrors().stream()
            .map(fe -> ValidationError.builder()
                .field(fe.getField())
                .message(fe.getDefaultMessage())
                .build())
            .toList();

        log.warn("Ошибка валидации: {}", validationErrors, e);

        return CommonResponse.<Void>builder()
            .errorMessage("Ошибка валидации")
            .validationErrors(validationErrors)
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResponse<?> handleInvalidFormatException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException ife) {
            String path = ife.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

            String errMessage = "Ошибка валидации, указан некорректный формат поля '" + path + "'";

            log.error(errMessage, e);

            return CommonResponse.builder()
                .errorMessage(errMessage)
                .build();
        }

        return handleException(e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonResponse<Void> handleException(Exception e) {
        log.error("Непредвиденное исключение: {}", e.getMessage(), e);

        return CommonResponse.<Void>builder()
            .id(UUID.randomUUID())
            .errorMessage("Непредвиденное исключение: " + e.getMessage())
            .build();
    }
}
