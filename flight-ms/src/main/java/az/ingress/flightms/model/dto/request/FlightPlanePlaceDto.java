package az.ingress.flightms.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightPlanePlaceDto {
    @NotNull
    private Long flightId;
    @NotNull
    private Long planePlaceId;
    @NotNull
    private Long ticketId;
    @NotBlank
    private String placeStatus;
    @NotBlank
    private String message;
}