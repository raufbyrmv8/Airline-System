package az.ingress.flightms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightDtoByCreatedOperator {
    Long operatorId;
    String operatorName;
    String operatorSurname;
    String operatorEmail;
    FlightDto flightDto;
}
