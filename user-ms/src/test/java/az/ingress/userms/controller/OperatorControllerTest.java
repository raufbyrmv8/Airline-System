package az.ingress.userms.controller;

import az.ingress.userms.model.dto.request.OperatorRequestDto;
import az.ingress.userms.model.dto.request.UserRequestDto;
import az.ingress.userms.service.OperatorService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorControllerTest {
    @Mock
    private  OperatorService operatorService;
    @InjectMocks
    private OperatorController operatorController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    OperatorRequestDto operatorRequestDto = new OperatorRequestDto(
            "rauf@gmail.com"
    );


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(operatorController).build();
        objectMapper = new ObjectMapper();

    }

    @Test
    void register() throws Exception{
        String json = objectMapper.writeValueAsString(operatorRequestDto);

        mockMvc.perform(
                put("/api/v1/operators/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        verify(operatorService, times(1)).registerOperator(any(OperatorRequestDto.class));
    }

    @Test
    void approvalOperator() throws Exception{
        String json = objectMapper.writeValueAsString(operatorRequestDto);

        mockMvc.perform(
                patch("/api/v1/operators/admin/operator/approval")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        verify(operatorService, times(1)).approvalOperator(any(OperatorRequestDto.class));
    }

    @Test
    void removeOperator() throws Exception{
        String json = objectMapper.writeValueAsString(operatorRequestDto);

        mockMvc.perform(
                patch("/api/v1/operators/admin/operator/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        verify(operatorService, times(1)).removeOperatorRole(any(OperatorRequestDto.class));
    }
}