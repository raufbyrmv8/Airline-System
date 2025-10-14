package az.ingress.flightms.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AirlineDto {
    @NotBlank(message = "Airline name cannot be blank")
    @NotNull(message = "Airline name is required")
    String name;
}
