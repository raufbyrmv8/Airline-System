package az.ingress.flightms.model.dto.response;


import az.ingress.flightms.model.enums.Airport;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class FlightResponseDto {
    private Airport from;
    private Airport to;
    private BigDecimal price;
    private Integer ticketCount;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private List<Map<String,Object>> availableSeats;
}
