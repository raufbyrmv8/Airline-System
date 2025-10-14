package az.ingress.flightms.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TicketConfirmationResponseDto {
    private final String createdTicketUrl;
}
