package geekcode.takatuf.Exception.Handler;

import geekcode.takatuf.Exception.Model.ApiException;
import geekcode.takatuf.Exception.Types.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    private ResponseEntity<Object> buildResponseEntity(Exception ex, HttpStatus status, String code,
            HttpServletRequest request) {
        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            localizedMessage = code;
        }

        log.error("Exception [{}]: {}", code, ex.getMessage(), ex);

        ApiException apiException = ApiException.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .localizedMessage(localizedMessage)
                .path(request.getRequestURI())
                .errorCode(code)
                .build();

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND, "error.resource.not_found", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST, "error.bad_request", request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED, "error.unauthorized", request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidation(ValidationException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.UNPROCESSABLE_ENTITY, "error.validation", request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED, "error.invalid_token", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

        ApiException apiException = ApiException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .localizedMessage("Validation failed")
                .path(request.getRequestURI())
                .errorCode("error.validation.fields")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("error", apiException);
        response.put("validationErrors", validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(
                violation -> validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        ApiException apiException = ApiException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Constraint violation")
                .localizedMessage("Constraint violation")
                .path(request.getRequestURI())
                .errorCode("error.constraint.violation")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("error", apiException);
        response.put("validationErrors", validationErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST, "error.illegal_argument", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND, "error.resource.not_found", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, "error.internal", request);
    }
}
