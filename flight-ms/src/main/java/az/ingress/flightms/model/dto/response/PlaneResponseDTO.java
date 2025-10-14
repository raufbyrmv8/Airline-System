package az.ingress.flightms.model.dto.response;

import az.ingress.flightms.model.dto.AirlineDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaneResponseDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private AirlineDto airline;
    private Set<PlanePlaceResponse> planePlaces;
}
