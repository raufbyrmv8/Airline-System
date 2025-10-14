package az.ingress.flightms.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record TicketConfirmationRequestDto(@NotNull Long ticketId,@NotNull String creditCardNo,@NotNull String creditCardName,@NotNull String creditCardExpiry,@NotNull String creditCardCvc) {
}
