package az.ingress.flightms.mapper;


import az.ingress.flightms.model.dto.FlightDto;
import az.ingress.flightms.model.dto.request.FlightRequestDto;
import az.ingress.flightms.model.entity.Flight;
import az.ingress.flightms.model.enums.Airport;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FlightMapper {

    @Mapping(target = "planeDetails.id", source = "plane.id")
    @Mapping(target = "planeDetails.name", source = "plane.name")
    @Mapping(target = "planeDetails.capacity", source = "plane.capacity")
    @Mapping(target = "planeDetails.planePlaces", ignore = true)
    FlightDto toDto(Flight flight);

    @Mapping(target = "plane.id", source = "planeId")
    Flight toEntity(FlightRequestDto flightRequestDto);

    @Mapping(target = "plane.id", source = "planeId")
    void updateFlight(@MappingTarget Flight flight, FlightRequestDto flightRequestDto);

    @Named("airportToString")
    default String airportToString(Airport airport) {
        return airport != null ? airport.name() : null;
    }

}
