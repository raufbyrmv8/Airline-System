package az.ingress.flightms.service;


import az.ingress.flightms.model.dto.FlightDto;
import az.ingress.flightms.model.dto.FlightDtoByCreatedOperator;
import az.ingress.flightms.model.dto.request.FlightRequestDto;

import java.util.List;

public interface FlightService {

    FlightDto getById(Long id);

    List<FlightDto> getAll();

    FlightDto create(FlightRequestDto dto);

    FlightDto update(Long id, FlightRequestDto dto);

    void delete(Long id);

    void approveFlight(Long id, String feedback);

    void rejectFlight(Long id, String feedback);

    List<FlightDtoByCreatedOperator> getPendingFlightsWithOperatorDetails();

    List<FlightDto> getFlightsByState(String state);
}
