package az.ingress.flightms.model.dto.request;

import jakarta.validation.constraints.NotNull;


public record TicketCreateRequestDto(@NotNull String passengerName, @NotNull String passengerSurname, @NotNull String passportNo, @NotNull String email, @NotNull String phone, @NotNull Long ticketRequestId) {
}
