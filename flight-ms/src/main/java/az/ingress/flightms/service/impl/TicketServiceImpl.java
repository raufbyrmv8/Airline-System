package az.ingress.flightms.service.impl;

import az.ingress.common.config.JwtSessionData;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.flightms.model.dto.request.TicketConfirmationRequestDto;
import az.ingress.flightms.model.dto.request.TicketCreateRequestDto;
import az.ingress.flightms.model.dto.request.TicketCreateResponseDto;
import az.ingress.flightms.model.dto.request.TicketRequestDto;
import az.ingress.flightms.model.dto.response.TicketConfirmationResponseDto;
import az.ingress.flightms.model.dto.response.TicketResponseDto;
import az.ingress.flightms.model.entity.*;
import az.ingress.flightms.model.enums.ApprovalState;
import az.ingress.flightms.model.enums.PlaceStatus;
import az.ingress.flightms.model.enums.TicketStatus;
import az.ingress.flightms.repository.*;
import az.ingress.flightms.service.TicketService;
import az.ingress.flightms.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;

import static az.ingress.flightms.model.enums.Exceptions.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {
    private final TicketRequestRepository ticketRequestRepository;
    private final JwtSessionData jwtSessionData;
    private final FlightPlanePlaceRepository flightPlanePlaceRepository;
    private final FlightRepository flightRepository;
    private final PlanePlaceRepository planePlaceRepository;
    private final TicketRepository ticketRepository;
    private final FileService fileService;


    public TicketResponseDto createTicketRequest(TicketRequestDto dto) {

        if (!flightRepository.existsByIdAndStatusAndTicketCountGreaterThanAndApprovalState(dto.flightId(), true,0, ApprovalState.APPROVED)) {
            throw new ApplicationException(NOT_FOUND, Flight.class.getSimpleName());
        }
        if (!flightRepository.existPlanePlaceByPlanePlaceId(dto.flightId(), dto.planePlaceId())) {
            throw new ApplicationException(NOT_FOUND, PlanePlace.class.getSimpleName());
        }
        if (flightPlanePlaceRepository.existsByFlightIdAndPlanePlaceIdAndStatus(dto.flightId(), dto.planePlaceId(), true)) {
            throw new ApplicationException(PLANE_PLACE_ALREADY_TAKEN);
        }

        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setFlightId(dto.flightId());
        ticketRequest.setExpiredDate(LocalDateTime.now().plusMinutes(5));
        ticketRequest.setPlanePlaceId(dto.planePlaceId());
        ticketRequest.setCreatedUserId(jwtSessionData.getUserId());
        ticketRequest = ticketRequestRepository.save(ticketRequest);
        return new TicketResponseDto(ticketRequest.getId());
    }

    @Override
    public TicketCreateResponseDto createTicket(TicketCreateRequestDto ticketCreateRequestDto) {
        TicketRequest ticketRequest = ticketRequestRepository.findByIdAndStatusAndExpiredDateGreaterThan(ticketCreateRequestDto.ticketRequestId(), true, LocalDateTime.now())
                .orElseThrow(() -> new ApplicationException(NOT_FOUND, TicketRequest.class.getSimpleName()));

        if (flightPlanePlaceRepository.existsByFlightIdAndPlanePlaceIdAndStatus(ticketRequest.getFlightId(), ticketRequest.getPlanePlaceId(), true)) {
            throw new ApplicationException(PLANE_PLACE_ALREADY_TAKEN);
        }

        if (ticketRepository.existsByTicketRequest(ticketRequest)) {
            throw new ApplicationException(TICKET_REQUEST_ALREADY_USED);
        }

        Flight flight = flightRepository.findById(ticketRequest.getFlightId()).orElseThrow(() -> new ApplicationException(NOT_FOUND, Flight.class.getSimpleName()));

        Ticket ticket = Ticket.builder()
                .flight(flight)
                .ticketRequest(ticketRequest)
                .boughtUserId(jwtSessionData.getUserId())
                .ticketStatus(TicketStatus.PENDING)
                .passengerName(ticketCreateRequestDto.passengerName())
                .passengerSurname(ticketCreateRequestDto.passengerSurname())
                .email(ticketCreateRequestDto.email())
                .phone(ticketCreateRequestDto.phone())
                .ticketNo(String.valueOf(System.currentTimeMillis()))
                .build();

        ticket = ticketRepository.save(ticket);

        return new TicketCreateResponseDto(ticket.getId());
    }

    @Override
    @Transactional
    public TicketConfirmationResponseDto confirm(TicketConfirmationRequestDto dto) {
        Ticket ticket = ticketRepository.findByIdAndStatusAndTicketStatus(dto.ticketId(), true,TicketStatus.PENDING).orElseThrow(() -> new ApplicationException(NOT_FOUND, Ticket.class.getSimpleName()));
        TicketRequest ticketRequest = ticket.getTicketRequest();
        PlanePlace planePlace = planePlaceRepository.findByIdAndStatus(ticketRequest.getPlanePlaceId(), true).orElseThrow(() -> new ApplicationException(NOT_FOUND, PlanePlace.class.getSimpleName()));
        Flight flight = ticket.getFlight();
        ticket.setTicketStatus(TicketStatus.CONFIRMED);

        FlightPlanePlace flightPlanePlace = FlightPlanePlace.builder()
                .planePlace(planePlace)
                .ticket(ticket)
                .placeStatus(PlaceStatus.BOOKED)
                .flight(flight)
                .build();

        flightPlanePlaceRepository.save(flightPlanePlace);
        flight.setTicketCount(flight.getTicketCount() - 1);

        String ticketContent = FileUtil.createTicketExample(ticket, flightPlanePlace);
        String ticketExampleFileUrl = fileService.createAndWriteToFile(ticketContent);
//        kafkaProducerService.sendTicketContent(new TicketMailDto(ticketContent, jwtSessionData.getUsername(), "Ticket Confirmation"));
        return new TicketConfirmationResponseDto(ticketExampleFileUrl);
    }

    @Override
    @Transactional
    public void refundTicket(Long ticketId) {
        Ticket ticket = ticketRepository
                .findByIdAndStatusAndTicketStatus(ticketId, true, TicketStatus.CONFIRMED)
                .orElseThrow(() -> new ApplicationException(NOT_FOUND, Ticket.class.getSimpleName()));

        if (!Objects.equals(ticket.getBoughtUserId(), jwtSessionData.getUserId())) {
            throw new ApplicationException(UNAUTHORIZED);
        }

        if (!ticket.getFlight().getDepartureTime().isAfter(LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay())) {
            throw new ApplicationException(TICKET_IS_NOT_REFUNDABLE);
        }

        ticket.setTicketStatus(TicketStatus.REFUNDED);

        Flight flight = ticket.getFlight();
        flight.setTicketCount(flight.getTicketCount() + 1);

        flightPlanePlaceRepository
                .findByStatusAndTicket(true,ticket)
                .orElseThrow(() -> new ApplicationException(NOT_FOUND, FlightPlanePlace.class.getSimpleName()))
                .setStatus(false);

//        kafkaProducerService.sendTicketContent(new TicketMailDto(createRefundedTicketContent(ticket), jwtSessionData.getUsername(), "Ticket Refunded"));
    }
}
