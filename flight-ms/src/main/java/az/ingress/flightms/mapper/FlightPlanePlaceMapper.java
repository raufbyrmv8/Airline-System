package az.ingress.flightms.mapper;
import az.ingress.flightms.model.dto.request.FlightPlanePlaceDto;
import az.ingress.flightms.model.entity.FlightPlanePlace;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FlightPlanePlaceMapper {

    @Mapping(source = "flight.id", target = "flightId")
    @Mapping(source = "planePlace.id", target = "planePlaceId")
    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(target = "placeStatus", source = "placeStatus")
    FlightPlanePlaceDto toDto(FlightPlanePlace flightPlanePlace);

    @Mapping(source = "flightId", target = "flight.id")
    @Mapping(source = "planePlaceId", target = "planePlace.id")
    @Mapping(source = "ticketId", target = "ticket.id")
    FlightPlanePlace toEntity(FlightPlanePlaceDto dto);

    @Mapping(source = "flightId", target = "flight.id")
    @Mapping(source = "planePlaceId", target = "planePlace.id")
    @Mapping(source = "ticketId", target = "ticket.id")
    void updateEntity(@MappingTarget FlightPlanePlace flightPlanePlace, FlightPlanePlaceDto dto);

}