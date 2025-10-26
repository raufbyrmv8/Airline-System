package az.ingress.userms.controller;

import az.ingress.userms.model.dto.ForgetPasswordDto;
import az.ingress.userms.model.dto.request.LoginRequestDto;
import az.ingress.userms.model.dto.request.UserRequestDto;
import az.ingress.userms.model.dto.response.TokenResponseDto;
import az.ingress.userms.service.AuthenticationService;
import az.ingress.userms.service.PasswordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    UserRequestDto userRequestDto = new UserRequestDto(
            "rauf@gmail.com",
            "Secret123!",
            "Rauf",
            "Bayramov");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUser() throws Exception {
        String json = objectMapper.writeValueAsString(userRequestDto);

        mockMvc.perform(
                post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(status().isOk());

        verify(authenticationService, times(1)).createUser(any(UserRequestDto.class));
    }


    @Test
    void verifyUser() throws Exception {
        String token = "sampleToken";
        mockMvc.perform(
                post("/api/v1/auth/verify")
                        .param("token", token)
        ).andExpect(status().isOk());

        verify(authenticationService, times(1)).verifyUser(eq(token));
    }

    @Test
    void login() throws Exception{
        LoginRequestDto loginRequest = new LoginRequestDto("rauf@gmail.com", "Secret123!");
        TokenResponseDto response = new TokenResponseDto("ACCESS_TOKEN", "REFRESH_TOKEN");

        when(authenticationService.login(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("ACCESS_TOKEN"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH_TOKEN"));

        verify(authenticationService, times(1)).login(any(LoginRequestDto.class));
    }

    @Test
    void refresh()throws Exception {
        String refreshToken = "REFRESH_123";
        TokenResponseDto response = new TokenResponseDto("NEW_ACCESS", "NEW_REFRESH");

        when(authenticationService.refresh(eq(refreshToken))).thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/auth/refresh")
                                .param("refreshToken", refreshToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("NEW_ACCESS"))
                .andExpect(jsonPath("$.refreshToken").value("NEW_REFRESH"));

        verify(authenticationService, times(1)).refresh(eq(refreshToken));
    }

    @Test
    void forgetPassword() throws Exception{
        ForgetPasswordDto forgetPasswordDto = new ForgetPasswordDto("reset@gmail.com");

        mockMvc.perform(
                post("/api/v1/auth/forget-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgetPasswordDto))
        ).andExpect(status().isOk());

        verify(passwordService).forgetPassword(eq(forgetPasswordDto.getEmail()));
    }
}