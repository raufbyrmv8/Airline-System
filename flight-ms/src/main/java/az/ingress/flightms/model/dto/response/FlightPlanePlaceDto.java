package az.ingress.flightms.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightPlanePlaceDto {
    private Long id;
    private Long flightId;
    private Long planePlaceId;
    private Long ticketId;
    private String placeStatus;
}