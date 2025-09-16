package az.ingress.common.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus status;
    private final String key;
    private final String detailsKey;
    Object[] args;

    public  ApplicationException(AbstractException exceptions, Object... args) {
        this.status = exceptions.getStatus();
        this.key = exceptions.getKey();
        this.detailsKey = exceptions.getDetail();
        this.args = args;
    }

}
