package az.ingress.flightms.model.dto;

import az.ingress.flightms.model.dto.response.PlaneResponseDTO;
import az.ingress.flightms.model.enums.ApprovalState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightDto {
    Long id;
    String from;
    String to;
    BigDecimal price;
    Integer ticketCount;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    String feedbackMessage;
    ApprovalState approvalState;
    PlaneResponseDTO planeDetails;
}
