package az.ingress.flightms.controller;

import az.ingress.flightms.model.dto.request.AirlineDto;
import az.ingress.flightms.model.dto.response.AirlineResponseDto;
import az.ingress.flightms.service.AirlineService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AirlineControllerTest {
    @Mock
    private AirlineService airlineService;
    @InjectMocks
    private AirlineController airlineController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AirlineResponseDto responseDto;
    private AirlineDto airlineDto;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(airlineController).build();
        objectMapper = new ObjectMapper();
        airlineDto = new AirlineDto();
        airlineDto.setName("AZAL");
        responseDto = new AirlineResponseDto();
        responseDto.setId(2l);
        responseDto.setName("AZAL");
    }

    @Test
    void createAirline() throws Exception{
        String json = objectMapper.writeValueAsString(airlineDto);
        mockMvc.perform(
                post("/api/v1/airline/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        verify(airlineService, times(1)).createAirline(any(AirlineDto.class));
    }

    @Test
    void findById() throws Exception{
        long id = 2l;
        when(airlineService.findById(anyLong())).thenReturn(responseDto);
        mockMvc.perform(
                        get("/api/v1/airline/{id}",id)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("AZAL"))
                .andExpect(jsonPath("$.id").value(2l));
        verify(airlineService,times(1)).findById(anyLong());
    }

    @Test
    void findAll() throws Exception {
        when(airlineService.findAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/airline")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("AZAL"));

        verify(airlineService, times(1)).findAll();
    }

    @Test
    void updateAirline() throws Exception{
        long id = 5L;
        when(airlineService.updateAirline(eq(id), any(AirlineDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/airline/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(airlineDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("AZAL"));

        verify(airlineService, times(1)).updateAirline(eq(id), any(AirlineDto.class));
    }

    @Test
    void findByName() throws Exception{
        String name = "AZAL";
        when(airlineService.findByAirlineByName(name)).thenReturn(airlineDto);

        mockMvc.perform(get("/api/v1/airline/find-name/{name}", name)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("AZAL"));

        verify(airlineService, times(1)).findByAirlineByName(name);
    }

    @Test
    void deleteAirline() throws Exception{
        long id = 9L;

        doNothing().when(airlineService).deleteAirline(id);

        mockMvc.perform(delete("/api/v1/airline/{id}", id))
                .andExpect(status().isOk());

        verify(airlineService, times(1)).deleteAirline(id);
    }
}