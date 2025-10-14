package az.ingress.flightms.model.dto.response;
import az.ingress.flightms.model.enums.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanePlaceResponse {
     private Long id;
     private Integer place;
     private Integer row;
     private Integer placeNumber;
     private PlaceType placeType;
}
