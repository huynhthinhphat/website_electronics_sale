package com.tip.b18.electronicsales.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseDTO<?> errorResponse(String message) {
        return new ResponseDTO<>("error", message, null);
    }

    @ExceptionHandler(value = CredentialsException.class)
    ResponseEntity<ResponseDTO<?>> handleCredentialsAndUnauthorizedException(CredentialsException e){
        ResponseDTO<?> responseDTO = errorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
    }

    @ExceptionHandler({IntegrityConstraintViolationException.class, AlreadyExistsException.class})
    ResponseEntity<ResponseDTO<?>> handleConflictRequest(Exception e){
        ResponseDTO<?> responseDTO = errorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ResponseDTO<?>> handlingAccessDeniedException(AccessDeniedException e){
        ResponseDTO<?> responseDTO = errorResponse(MessageConstant.ERROR_ACCESS_DENIED);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
    }

    @ExceptionHandler({InvalidValueException.class,
            DateTimeParseException.class,
            IllegalStateException.class,
            InsufficientStockException.class,
            HttpMessageNotReadableException.class,
            InvalidPasswordException.class,
            MethodArgumentNotValidException.class,
            NotFoundException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            CloudinaryDeleteException.class,
            PayOSException.class})
    public ResponseEntity<ResponseDTO<?>> handleBadRequest(Exception e) {
        String message = null;
        if (e instanceof MethodArgumentNotValidException) {
            message = Objects.requireNonNull(((MethodArgumentNotValidException) e).getFieldError()).getDefaultMessage();
        } else if(e instanceof MissingServletRequestParameterException){
            message = MessageConstant.ERROR_VALUE_REQUIRED;
        } else if(e instanceof MethodArgumentTypeMismatchException){
            message = String.format(MessageConstant.INVALID_PARAM, ((MethodArgumentTypeMismatchException) e).getName());
        } else if(e instanceof HttpMessageNotReadableException){
            Throwable cause = e.getCause();
            if(cause instanceof InvalidFormatException invalidFormatException){
                List<JsonMappingException.Reference> path = invalidFormatException.getPath();
                if (!path.isEmpty()) {
                    String fieldName = path.get(0).getFieldName();
                    message = String.format(MessageConstant.INVALID_FIELD_FORMAT, fieldName);
                }
            }else{
                message = MessageConstant.INVALID_JSON_FORMAT;
            }
        } else if(e instanceof DateTimeParseException){
            message = MessageConstant.INVALID_DATE_FORMAT_MESSAGE;
        }else {
            message = e.getMessage();
        }
        ResponseDTO<?> responseDTO = errorResponse(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

}
