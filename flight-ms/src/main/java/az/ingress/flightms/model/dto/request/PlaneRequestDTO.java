package az.ingress.flightms.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaneRequestDTO {
    private String name;
    private Long airlineId;
    private Set<Long> planePlacesIds;
}
