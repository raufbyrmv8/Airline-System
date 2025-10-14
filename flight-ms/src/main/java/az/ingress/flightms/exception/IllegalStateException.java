package az.ingress.flightms.exception;

import az.ingress.common.model.exception.AbstractException;
import az.ingress.common.model.exception.ApplicationException;

public class IllegalStateException extends ApplicationException {
    public IllegalStateException(AbstractException exceptions, Object... args) {
        super(exceptions, args);
    }
}
