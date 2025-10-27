package az.ingress.flightms.controller;

import az.ingress.flightms.model.dto.request.AirlineDto;
import az.ingress.flightms.model.dto.request.PlanePlaceRequest;
import az.ingress.flightms.model.dto.response.PlanePlaceResponse;
import az.ingress.flightms.model.enums.PlaceType;
import az.ingress.flightms.service.BookingService;
import az.ingress.flightms.service.PlanePlaceService;
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

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class PlanePlaceControllerTest {
    @Mock
    private PlanePlaceService planePlaceService;
    @InjectMocks
    private PlanePlaceController planePlaceController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    PlanePlaceRequest planePlaceRequest = new PlanePlaceRequest(
            2,
            1,
            4,
            PlaceType.NORMAL
    );
    PlanePlaceResponse planePlaceResponse = new PlanePlaceResponse(
            2l,
            2,
            1,
            4,
            PlaceType.NORMAL
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(planePlaceController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create()throws Exception {
        String json = objectMapper.writeValueAsString(planePlaceRequest);
        mockMvc.perform(
                post("/api/v1/plane-place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isCreated());

        verify(planePlaceService, times(1)).createPlanePlace(any(PlanePlaceRequest.class));
    }

    @Test
    void getById() throws Exception{
        long id = 2l;
        when(planePlaceService.getPlanePlaceById(anyLong())).thenReturn(planePlaceResponse);
        mockMvc.perform(
                        get("/api/v1/plane-place/{id}",id)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2l));
        verify(planePlaceService,times(1)).getPlanePlaceById(anyLong());
    }

    @Test
    void getAll() throws Exception{
        when(planePlaceService.getAllPlanePlaces()).thenReturn(Set.of(planePlaceResponse));

        mockMvc.perform(get("/api/v1/plane-place")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2l));

        verify(planePlaceService, times(1)).getAllPlanePlaces();
    }

    @Test
    void update() throws Exception{
        long id = 5L;
        when(planePlaceService.updatePlanePlace(eq(id), any(PlanePlaceRequest.class))).thenReturn(planePlaceResponse);

        mockMvc.perform(put("/api/v1/plane-place/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planePlaceRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2l));

        verify(planePlaceService, times(1)).updatePlanePlace(eq(id), any(PlanePlaceRequest.class));
    }
}