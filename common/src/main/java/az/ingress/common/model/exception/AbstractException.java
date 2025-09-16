package az.ingress.common.model.exception;

import org.springframework.http.HttpStatus;

public interface AbstractException {
    String getKey();
    String getDetail();
    HttpStatus getStatus();
}
