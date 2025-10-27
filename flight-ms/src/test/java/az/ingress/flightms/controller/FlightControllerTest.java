package az.ingress.flightms.controller;

import az.ingress.flightms.model.dto.FlightDto;
import az.ingress.flightms.model.dto.FlightDtoByCreatedOperator;
import az.ingress.flightms.model.dto.request.FlightRequestDto;
import az.ingress.flightms.service.BookingService;
import az.ingress.flightms.service.FlightService;
import az.ingress.flightms.service.PlanePlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {
    @Mock
    private FlightService flightService;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private FlightController flightController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String BASE = "/api/v1/flights";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    void getById() throws Exception{
        Long id = 42L;
        FlightDto dto = new FlightDto();
        dto.setId(id);
        dto.setFrom("GYD");
        dto.setTo("IST");
        dto.setPrice(new BigDecimal("199.99"));

        when(flightService.getById(id)).thenReturn(dto);

        mockMvc.perform(get(BASE + "/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(flightService, times(1)).getById(eq(id));
    }

    @Test
    void getAll() throws Exception {
        // given
        FlightDto f1 = new FlightDto();
        f1.setId(1L);
        f1.setFrom("GYD");
        f1.setTo("IST");
        f1.setPrice(new BigDecimal("199.99"));

        FlightDto f2 = new FlightDto();
        f2.setId(2L);
        f2.setFrom("GYD");
        f2.setTo("DXB");
        f2.setPrice(new BigDecimal("249.50"));

        List<FlightDto> list = List.of(f1, f2);
        Mockito.when(flightService.getAll()).thenReturn(list);

        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));

        verify(flightService, times(1)).getAll();
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void getFlightsByStateWithOperatorDetails() throws Exception {
        var d1 = new FlightDtoByCreatedOperator();
        var d2 = new FlightDtoByCreatedOperator();
        List<FlightDtoByCreatedOperator> list = List.of(d1, d2);

        Mockito.when(flightService.getPendingFlightsWithOperatorDetails()).thenReturn(list);

        mockMvc.perform(get(BASE + "/pending-approval"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));

        verify(flightService, times(1)).getPendingFlightsWithOperatorDetails();
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void getFlightsByState() throws Exception{
        String state = "PENDING";
        FlightDto f1 = new FlightDto();
        f1.setId(10L);
        f1.setFrom("GYD");
        f1.setTo("IST");
        f1.setPrice(new BigDecimal("120.00"));

        FlightDto f2 = new FlightDto();
        f2.setId(11L);
        f2.setFrom("GYD");
        f2.setTo("DXB");
        f2.setPrice(new BigDecimal("180.50"));


        List<FlightDto> list = List.of(f1, f2);
        Mockito.when(flightService.getFlightsByState(state)).thenReturn(list);

        mockMvc.perform(get(BASE + "/approval-state/{state}", state))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));

        verify(flightService, times(1)).getFlightsByState(eq(state));
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void create() throws Exception{

        FlightRequestDto req = new FlightRequestDto();
        req.setFrom("GYD");
        req.setTo("IST");
        req.setPrice(new BigDecimal("199.99"));
        req.setPlaneId(100L);

        FlightDto resp = new FlightDto();
        resp.setId(1L);
        resp.setFrom(req.getFrom());
        resp.setTo(req.getTo());
        resp.setPrice(req.getPrice());
        resp.setDepartureTime(req.getDepartureTime());
        resp.setArrivalTime(req.getArrivalTime());

        Mockito.when(flightService.create(any(FlightRequestDto.class))).thenReturn(resp);

        // when + then
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        verify(flightService, times(1)).create(any(FlightRequestDto.class));
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void update() throws Exception{
        Long id = 15L;
        FlightRequestDto req = new FlightRequestDto();
        req.setFrom("GYD");
        req.setTo("IST");
        req.setPrice(new BigDecimal("220.00"));
        req.setPlaneId(200L);

        FlightDto resp = new FlightDto();
        resp.setId(id);
        resp.setFrom(req.getFrom());
        resp.setTo(req.getTo());
        resp.setPrice(req.getPrice());
        resp.setDepartureTime(req.getDepartureTime());
        resp.setArrivalTime(req.getArrivalTime());

        Mockito.when(flightService.update(eq(id), any(FlightRequestDto.class))).thenReturn(resp);

        mockMvc.perform(put(BASE + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        verify(flightService, times(1)).update(eq(id), any(FlightRequestDto.class));
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void reject() throws Exception{
        Long id = 9L;
        String feedback = "Not matching schedule";
        Mockito.doNothing().when(flightService).rejectFlight(eq(id), eq(feedback));

        mockMvc.perform(post(BASE + "/reject/{id}", id)
                        .param("feedback", feedback))
                .andExpect(status().isOk())
                .andExpect(content().string("Flight with id " + id + " rejected" + "Email sent to operator"));

        verify(flightService, times(1)).rejectFlight(eq(id), eq(feedback));
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void approve() throws Exception{
        Long id = 21L;
        String feedback = "All checks passed";
        Mockito.doNothing().when(flightService).approveFlight(eq(id), eq(feedback));

        mockMvc.perform(post(BASE + "/approve/{id}", id)
                        .param("feedback", feedback))
                .andExpect(status().isOk())
                .andExpect(content().string("Flight with id " + id + " approved" + "Email sent to operator"));

        verify(flightService, times(1)).approveFlight(eq(id), eq(feedback));
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void delete() throws Exception{
        Long id = 33L;
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(BASE + "/{id}", id)
        ).andExpect(status().isNoContent());

        verify(flightService, times(1)).delete(eq(id));
        verifyNoMoreInteractions(flightService);
        verifyNoInteractions(bookingService);
    }

    @Test
    void cancelFlight()throws Exception {
        Long id = 77L;

        mockMvc.perform(put(BASE + "/flight/cancel/{id}", id))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).cancelFlight(eq(id));
        verifyNoMoreInteractions(bookingService);
        verifyNoInteractions(flightService);
    }
}