package az.ingress.flightms.model.enums;


import az.ingress.common.model.exception.AbstractException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum Exceptions implements AbstractException {
    NOT_FOUND("exception.not.found", "exception.not.found.detail", HttpStatus.NOT_FOUND),
    FLIGHT_NOT_FOUND("exception.flight.not.found", "exception.flight.not.found.detail", HttpStatus.NOT_FOUND),
    PLANE_NOT_FOUND("exception.plane.not.found", "exception.plane.not.found.detail", HttpStatus.NOT_FOUND),
    PLANE_PLACES_NOT_FOUND("exception.plane.place.not.found", "exception.plane.place.not.found.detail", HttpStatus.NOT_FOUND),
    STATE_IS_NOT_PENDING("exception.flight.not.pending.state", "exception.flight.not.pending.state.detail", HttpStatus.CONFLICT),
    TICKET_IS_NOT_REFUNDABLE("exception.ticket.not.refundable", "exception.ticket.not.refundable.detail", HttpStatus.CONFLICT),
    TICKET_REQUEST_ALREADY_USED("exception.ticket.request.already.used", "exception.ticket.request.already.used.detail", HttpStatus.BAD_REQUEST),
    SOMETHING_WENT_WRONG("exception.something.went.wrong", "exception.something.went.wrong.detail", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("exception.unauthorized", "exception.unauthorized.detail", HttpStatus.UNAUTHORIZED),
    UNIQUE_CONSTRAINT("exception.unique.constraint", "exception.unique.constraint.detail", HttpStatus.BAD_REQUEST),
    PLANE_PLACE_ALREADY_TAKEN("exception.plane.place.already.taken", "exception.plane.place.already.taken.detail", HttpStatus.BAD_REQUEST);

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
