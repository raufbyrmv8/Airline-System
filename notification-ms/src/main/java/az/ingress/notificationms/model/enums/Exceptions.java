package az.ingress.notificationms.model.enums;
import az.ingress.common.model.exception.AbstractException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum Exceptions implements AbstractException {
    NOT_FOUND("exception.not.found","exception.not.found.detail", HttpStatus.NOT_FOUND),
    APPLICATION_MAIL_MESSAGE_FAILURE("exception.application.mail.message.failure", "exception.application.mail.message.failure.detail", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String key;
    private final String detailKey;
    private final HttpStatus httpStatus;

    Exceptions(String key, String detailKey, HttpStatus httpStatus) {
        this.key = key;
        this.detailKey = detailKey;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getDetail() {
        return detailKey;
    }

    @Override
    public HttpStatus getStatus() {
        return getHttpStatus();
    }
}
