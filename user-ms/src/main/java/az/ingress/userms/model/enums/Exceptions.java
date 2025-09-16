package az.ingress.userms.model.enums;


import az.ingress.common.model.exception.AbstractException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum Exceptions implements AbstractException {
    NOT_FOUND("exception.not.found","exception.not.found.detail", HttpStatus.NOT_FOUND),
    BAD_CREDENTIALS("exception.bad.credentials", "exception.bad.credentials.detail", HttpStatus.UNAUTHORIZED),
    USERNAME_ALREADY_EXISTS("exception.username.already.exists", "exception.username.already.exists.detail", HttpStatus.CONFLICT),
    PASSWORD_IS_INCORRECT_EXCEPTION("exception.password.is.not.correct", "exception.password.is.not.correct.detail", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND_EXCEPTION("exception.token.not.found", "exception.token.not.found.detail", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED_EXCEPTION("exception.token.expired", "exception.token.expired.detail", HttpStatus.UNAUTHORIZED),
    PASSWORD_MISMATCH_EXCEPTION("exception.password.mismatch", "exception.password.mismatch.detail", HttpStatus.BAD_REQUEST);

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
