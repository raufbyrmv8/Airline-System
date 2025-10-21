package az.ingress.flightms.service.impl;
import az.ingress.common.config.JwtSessionData;
import az.ingress.common.model.dto.TicketMailDto;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.flightms.model.dto.response.BookingSearchResponseDto;
import az.ingress.flightms.model.dto.response.FlightResponseDto;
import az.ingress.flightms.model.entity.Flight;
import az.ingress.flightms.model.enums.TicketStatus;
import az.ingress.flightms.producer.KafkaProducer;
import az.ingress.flightms.repository.FlightPlanePlaceRepository;
import az.ingress.flightms.repository.FlightRepository;
import az.ingress.flightms.repository.PlanePlaceRepository;
import az.ingress.flightms.repository.TicketRepository;
import az.ingress.flightms.service.BookingService;
import az.ingress.flightms.service.kafka.KafkaProducerService;
import az.ingress.flightms.specification.FlightSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static az.ingress.flightms.model.enums.Exceptions.NOT_FOUND;
import static az.ingress.flightms.util.FileUtil.createCancelFlightContent;
import static az.ingress.flightms.util.FileUtil.createRefundedTicketContent;
import static az.ingress.flightms.util.MapperUtil.mapDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final FlightSpecification flightSpecification;
    private final FlightRepository flightRepository;
    private final PlanePlaceRepository planePlaceRepository;
    private final FlightPlanePlaceRepository flightPlanePlaceRepository;
    private final TicketRepository ticketRepository;
    private final JwtSessionData jwtSessionData;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public List<BookingSearchResponseDto> search(String from, String to, String date, BigDecimal price) {
        Specification<Flight> readySpecification = flightSpecification.search(from, to, convertToDateTime(date), price);
        List<Flight> flights = flightRepository.findAll(readySpecification);
        List<BookingSearchResponseDto> res = new ArrayList<>(flights.size());
        mapDto(flights, res);
        return res;
    }

    @Override
    public FlightResponseDto availableSeats(Long flightId) {
        Flight flight = flightRepository.findByIdAndStatus(flightId, true).orElseThrow(() -> new ApplicationException(NOT_FOUND, Flight.class.getSimpleName()));
        List<Integer> capturedSeats = flightPlanePlaceRepository.findPlaceNumberByFlightId(flightId);
        List<Map<String, Object>> availableSeats = planePlaceRepository.findPlanePlaceByFlightId(flight.getPlane().getId(), capturedSeats);

        return FlightResponseDto.builder()
                .from(flight.getFrom())
                .to(flight.getTo())
                .price(flight.getPrice())
                .ticketCount(flight.getTicketCount())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .availableSeats(availableSeats)
                .build();
    }

    private LocalDateTime convertToDateTime(String date) {
        if (date != null && !date.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(date, formatter);
            } catch (DateTimeParseException e) {
                log.error("Invalid date format: {}", date, e);
                return null;
            }
        }
        return null;
    }

    @Override
    public void cancelFlight(Long flightId) {
        Flight flight = flightRepository.findByIdAndStatus(flightId, true).orElseThrow(() -> new ApplicationException(NOT_FOUND, Flight.class.getSimpleName()));
        flight.setStatus(false);
        flightPlanePlaceRepository.findByStatusAndFlight(true, flight).forEach(flightPlanePlace -> {
            flightPlanePlace.setStatus(false);
            flightPlanePlaceRepository.save(flightPlanePlace);
        });
        ticketRepository.findByStatusAndFlightAndTicketStatus(true,flight, TicketStatus.CONFIRMED).forEach(ticket -> {
            ticket.setStatus(false);
            ticketRepository.save(ticket);
            kafkaProducerService.sendTicketContent(new TicketMailDto(createCancelFlightContent(ticket),jwtSessionData.getUsername(),"Ticket Cancelled"));
        });
        flightRepository.save(flight);
    }
}
