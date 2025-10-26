package az.ingress.userms.controller;

import az.ingress.userms.model.dto.ChangePasswordDto;
import az.ingress.userms.model.dto.ResetPasswordDto;
import az.ingress.userms.model.dto.request.OperatorRequestDto;
import az.ingress.userms.service.OperatorService;
import az.ingress.userms.service.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PasswordControllerTest {
    @Mock
    private PasswordService passwordService;
    @Mock
    private HttpServletResponse httpServletResponse;
    @InjectMocks
    private PasswordController passwordController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    ChangePasswordDto changePasswordDto = new ChangePasswordDto(
            "12345",
            "123456",
            "123456"
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void changePassword() throws Exception{
        String json = objectMapper.writeValueAsString(changePasswordDto);

        mockMvc.perform(
                put("/api/v1/user/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(json)
        ).andExpect(status().isOk());

        verify(passwordService).changePassword(
                eq(changePasswordDto),
                eq("Bearer token"),
                any(HttpServletResponse.class)
        );
    }

    @Test
    void setNewPassword() throws Exception{
        ResetPasswordDto body = new ResetPasswordDto("newPass123", "newPass123");
        String json = objectMapper.writeValueAsString(body);
        String token = "TKN-123";

        mockMvc.perform(
                        put("/api/v1/user/password/reset")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("token", token)
                                .content(json)
                )
                .andExpect(status().isOk());

        verify(passwordService).setNewPassword(eq(token), eq(body));
        verifyNoMoreInteractions(passwordService);
    }
}