package az.ingress.flightms.model.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//@ValidFlightTimes
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlightRequestDto {

//    @ValidAirportCode(message = "Invalid departure airport code")
    String from;

//    @ValidAirportCode(message = "Invalid destination airport code")
    String to;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    BigDecimal price;

    @NotNull(message = "Departure time is required")
    LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    LocalDateTime arrivalTime;

    @NotNull(message = "Plane ID is required")
    Long planeId;

}
