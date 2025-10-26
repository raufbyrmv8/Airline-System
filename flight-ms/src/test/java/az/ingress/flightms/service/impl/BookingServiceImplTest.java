package az.ingress.flightms.service.impl;

import az.ingress.common.config.JwtSessionData;
import az.ingress.common.model.dto.TicketMailDto;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.flightms.model.dto.response.BookingSearchResponseDto;
import az.ingress.flightms.model.dto.response.FlightResponseDto;
import az.ingress.flightms.model.entity.Flight;
import az.ingress.flightms.model.entity.FlightPlanePlace;
import az.ingress.flightms.model.entity.Plane;
import az.ingress.flightms.model.entity.Ticket;
import az.ingress.flightms.model.enums.Airport;
import az.ingress.flightms.model.enums.TicketStatus;
import az.ingress.flightms.repository.FlightPlanePlaceRepository;
import az.ingress.flightms.repository.FlightRepository;
import az.ingress.flightms.repository.PlanePlaceRepository;
import az.ingress.flightms.repository.TicketRepository;
import az.ingress.flightms.service.kafka.KafkaProducerService;
import az.ingress.flightms.specification.FlightSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock private FlightSpecification flightSpecification;
    @Mock private FlightRepository flightRepository;
    @Mock private PlanePlaceRepository planePlaceRepository;
    @Mock private FlightPlanePlaceRepository flightPlanePlaceRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private JwtSessionData jwtSessionData;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void search() {
        String from = "BAK";
        String to = "IST";
        String date = "2025-11-01 14:30";
        BigDecimal price = new BigDecimal("199.99");

        @SuppressWarnings("unchecked")
        Specification<Flight> spec = mock(Specification.class);

        when(flightSpecification.search(
                eq(from),
                eq(to),
                argThat(dt -> dt != null
                        && dt.equals(LocalDateTime.of(2025, 11, 1, 14, 30))),
                eq(price)
        )).thenReturn(spec);

        when(flightRepository.findAll(spec)).thenReturn(Collections.emptyList());

        List<BookingSearchResponseDto> result = bookingService.search(from, to, date, price);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(flightSpecification, times(1))
                .search(eq(from), eq(to),
                        argThat(dt -> dt != null && dt.equals(LocalDateTime.of(2025, 11, 1, 14, 30))),
                        eq(price));
        verify(flightRepository, times(1)).findAll(spec);
        verifyNoMoreInteractions(flightRepository, flightSpecification);
    }


    @Test
    void search_whenInvalidDate_thenPassNullDateToSpec_andReturnEmptyList() {
        // given
        String from = "BAK";
        String to = "IST";
        String badDate = "01-11-2025";
        BigDecimal price = new BigDecimal("199.99");

        @SuppressWarnings("unchecked")
        Specification<Flight> spec = mock(Specification.class);

        when(flightSpecification.search(eq(from), eq(to), isNull(), eq(price))).thenReturn(spec);
        when(flightRepository.findAll(spec)).thenReturn(Collections.emptyList());

        List<BookingSearchResponseDto> result = bookingService.search(from, to, badDate, price);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(flightSpecification).search(eq(from), eq(to), isNull(), eq(price));
        verify(flightRepository).findAll(spec);
        verifyNoMoreInteractions(flightRepository, flightSpecification);
    }
    @Test
    void availableSeats() {
        Long flightId = 100L;

        Plane plane = new Plane(); plane.setId(42L);
        Flight flight = new Flight();
        flight.setId(flightId);
        flight.setFrom(Airport.GYD);
        flight.setTo(Airport.IST);
        flight.setPrice(new BigDecimal("249.50"));
        flight.setTicketCount(120);
        flight.setDepartureTime(LocalDateTime.of(2025, 11, 1, 9, 30));
        flight.setArrivalTime(LocalDateTime.of(2025, 11, 1, 11, 55));
        flight.setPlane(plane);
        flight.setStatus(true);

        when(flightRepository.findByIdAndStatus(flightId, true)).thenReturn(Optional.of(flight));
        when(flightPlanePlaceRepository.findPlaceNumberByFlightId(flightId)).thenReturn(List.of(1,2,3));
        when(planePlaceRepository.findPlanePlaceByFlightId(42L, List.of(1,2,3)))
                .thenReturn(List.of(Map.of("number",4,"class","ECONOMY")));

        FlightResponseDto resp = bookingService.availableSeats(flightId);

        assertNotNull(resp.getPrice());
        assertEquals(Airport.GYD, resp.getFrom());
        assertEquals(Airport.IST, resp.getTo());
        assertEquals(0, resp.getPrice().compareTo(new BigDecimal("249.50")));
        verify(flightRepository).findByIdAndStatus(flightId, true);
    }
    @Test
    void availableSeats_notFound_throws() {
        when(flightRepository.findByIdAndStatus(999L, true)).thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> bookingService.availableSeats(999L));
        verify(flightRepository).findByIdAndStatus(999L, true);
    }

    @Test
    void cancelFlight() {
        Long flightId = 123L;

        Flight flight = new Flight();
        flight.setId(flightId);
        flight.setStatus(true);

        flight.setFrom(Airport.GYD);
        flight.setTo(Airport.IST);
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));
        flight.setPrice(new BigDecimal("199.99"));

        FlightPlanePlace fpp1 = new FlightPlanePlace(); fpp1.setStatus(true);
        FlightPlanePlace fpp2 = new FlightPlanePlace(); fpp2.setStatus(true);

        Ticket t1 = new Ticket();
        t1.setStatus(true);
        t1.setTicketStatus(TicketStatus.CONFIRMED);
        t1.setFlight(flight);
        Ticket t2 = new Ticket();
        t2.setStatus(true);
        t2.setTicketStatus(TicketStatus.CONFIRMED);
        t2.setFlight(flight);

        when(flightRepository.findByIdAndStatus(flightId, true)).thenReturn(Optional.of(flight));
        when(flightPlanePlaceRepository.findByStatusAndFlight(true, flight)).thenReturn(List.of(fpp1, fpp2));
        when(ticketRepository.findByStatusAndFlightAndTicketStatus(true, flight, TicketStatus.CONFIRMED))
                .thenReturn(List.of(t1, t2));
        when(jwtSessionData.getUsername()).thenReturn("user@example.com");

        bookingService.cancelFlight(flightId);

        ArgumentCaptor<Flight> flightCaptor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository).save(flightCaptor.capture());
        assertFalse(flightCaptor.getValue().getStatus());

        ArgumentCaptor<FlightPlanePlace> fppCaptor = ArgumentCaptor.forClass(FlightPlanePlace.class);
        verify(flightPlanePlaceRepository, times(2)).save(fppCaptor.capture());
        fppCaptor.getAllValues().forEach(fpp -> assertFalse(fpp.getStatus()));

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository, times(2)).save(ticketCaptor.capture());
        ticketCaptor.getAllValues().forEach(t -> assertFalse(t.getStatus()));

        ArgumentCaptor<TicketMailDto> mailCaptor = ArgumentCaptor.forClass(TicketMailDto.class);
        verify(kafkaProducerService, times(2)).sendTicketContent(mailCaptor.capture());
        mailCaptor.getAllValues().forEach(dto -> {
            assertNotNull(dto.getSubject());
            assertEquals("user@example.com", dto.getEmail());
            assertEquals("Ticket Cancelled", dto.getSubject());
        });

        verify(flightRepository).findByIdAndStatus(flightId, true);
        verify(flightPlanePlaceRepository).findByStatusAndFlight(true, flight);
        verify(ticketRepository).findByStatusAndFlightAndTicketStatus(true, flight, TicketStatus.CONFIRMED);

        verifyNoMoreInteractions(flightRepository, flightPlanePlaceRepository, ticketRepository, kafkaProducerService);
        verifyNoInteractions(planePlaceRepository, flightSpecification);
    }
    @Test
    void cancelFlight_notFound_throwsApplicationException() {
        Long flightId = 999L;
        when(flightRepository.findByIdAndStatus(flightId, true)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> bookingService.cancelFlight(flightId));

        verify(flightRepository).findByIdAndStatus(flightId, true);
        verifyNoMoreInteractions(flightRepository);
        verifyNoInteractions(flightPlanePlaceRepository, ticketRepository, kafkaProducerService,
                planePlaceRepository, jwtSessionData, flightSpecification);
    }
}