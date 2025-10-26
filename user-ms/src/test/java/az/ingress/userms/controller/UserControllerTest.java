package az.ingress.userms.controller;

import az.ingress.userms.model.dto.request.OperatorRequestDto;
import az.ingress.userms.model.dto.response.UserResponseDto;
import az.ingress.userms.service.PasswordService;
import az.ingress.userms.service.UserService;
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


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserDetailsById() throws Exception{
        long id = 42L;
        UserResponseDto dto =
                new UserResponseDto(id, "Rauf", "Bayramov", "rauf@example.com");

        when(userService.getUserDetailsById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/info/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Rauf"))
                .andExpect(jsonPath("$.surname").value("Bayramov"))
                .andExpect(jsonPath("$.email").value("rauf@example.com"));

        verify(userService).getUserDetailsById(id);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserDetailsByEmail() throws Exception{
        String email = "rauf@example.com";
        UserResponseDto dto = new UserResponseDto(7L, "Rauf", "Bayramov", email);

        when(userService.getUserDetailsByEmail(email)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/get/info/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Rauf"))
                .andExpect(jsonPath("$.surname").value("Bayramov"))
                .andExpect(jsonPath("$.email").value(email));

        verify(userService).getUserDetailsByEmail(email);
        verifyNoMoreInteractions(userService);
    }
}