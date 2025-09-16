package az.ingress.common.model.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class Handler extends DefaultErrorAttributes {
    private final MessageSource messageSource;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionResponse> handleException(ApplicationException e, WebRequest webRequest) {
        e.printStackTrace();
        Map<String, Object> errorAttributes = getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(getMessage(getMessage(e.getKey())));
        response.setDetails(getMessage(e.getDetailsKey(), e.getArgs()));
        response.setPath(((ServletRequestAttributes) webRequest).getRequest().getServletPath());
        response.setStatus(e.getStatus().value());
        response.setTimestamp(errorAttributes.get("timestamp").toString());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse();
        String errorMessage = "Invalid enum value provided: " + ex.getMessage();
        response.setMessage(errorMessage);
        response.setDetails("Please check the provided values for enum fields.");
        response.setPath(((ServletRequestAttributes) webRequest).getRequest().getServletPath());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest webRequest) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = messageSource.getMessage(error.getDefaultMessage(), null, error.getDefaultMessage(), LocaleContextHolder.getLocale());
            validationErrors.put(field, message);
        });

        // Build ExceptionResponse
        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(getMessage("exception.validation.failed"));
        response.setDetails(getMessage("exception.validation.failed.details"));
        response.setValidationErrors(validationErrors);
        response.setPath(((ServletRequestAttributes) webRequest).getRequest().getServletPath());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    public String getMessage(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error("please localize the message for key: {}", key);
            return key;
        }
    }
}
