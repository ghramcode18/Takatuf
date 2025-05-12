package geekcode.takatuf.Exception.Handler;

import geekcode.takatuf.Exception.Model.ApiException;
import geekcode.takatuf.Exception.Types.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR, "error.internal", request);
    }
}
