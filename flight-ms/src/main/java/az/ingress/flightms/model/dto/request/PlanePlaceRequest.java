package az.ingress.flightms.model.dto.request;


import az.ingress.flightms.model.enums.PlaceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanePlaceRequest {
    private Integer place;    // optional when creating a new plane place
    private Integer row;     // optional when creating a new plane place
    private Integer placeNumber;
    private PlaceType placeType;
}
