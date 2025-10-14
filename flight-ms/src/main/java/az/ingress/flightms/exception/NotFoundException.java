package az.ingress.flightms.exception;


import az.ingress.common.model.exception.AbstractException;
import az.ingress.common.model.exception.ApplicationException;

public class NotFoundException extends ApplicationException {
    public NotFoundException(AbstractException exceptions, Object... args) {
        super(exceptions, args);
    }
}
