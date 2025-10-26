package az.ingress.flightms.controller;

import az.ingress.flightms.model.dto.response.FlightResponseDto;
import az.ingress.flightms.model.enums.Airport;
import az.ingress.flightms.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void search() throws Exception{
        String to = "IST";
        String from = "GYD";
        String date = "2025-11-01 14:30";
        BigDecimal initialPrice = new BigDecimal("199.99");

        when(bookingService.search(eq(to), eq(from), eq(date), eq(initialPrice)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/search")
                        .param("to", to)
                        .param("from", from)
                        .param("date", date)
                        .param("initialPrice", initialPrice.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookingService, times(1))
                .search(eq(to), eq(from), eq(date), eq(initialPrice));
        verifyNoMoreInteractions(bookingService);
    }
    @Test
    void search_withoutParams_allNulls_okAndEmptyArray() throws Exception {
        when(bookingService.search(isNull(), isNull(), isNull(), isNull()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/search")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookingService).search(isNull(), isNull(), isNull(), isNull());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void availableSeats() throws Exception{
        long flightId = 5L;

        FlightResponseDto resp = FlightResponseDto.builder()
                .from(Airport.GYD)
                .to(Airport.IST)
                .price(new BigDecimal("149.50"))
                .ticketCount(120)
                .departureTime(LocalDateTime.of(2025, 11, 1, 9, 30))
                .arrivalTime(LocalDateTime.of(2025, 11, 1, 11, 55))
                .availableSeats(List.of())
                .build();

        when(bookingService.availableSeats(flightId)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/booking/available-seats/{id}", flightId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.from").value("GYD"))
                .andExpect(jsonPath("$.to").value("IST"))
                .andExpect(jsonPath("$.price").value(149.50))
                .andExpect(jsonPath("$.ticketCount").value(120));

        verify(bookingService).availableSeats(flightId);
        verifyNoMoreInteractions(bookingService);
    }
}