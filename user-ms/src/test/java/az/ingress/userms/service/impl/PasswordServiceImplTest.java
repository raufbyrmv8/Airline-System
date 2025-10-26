package az.ingress.userms.service.impl;

import az.ingress.common.kafka.UserResetPasswordDto;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.common.service.JwtService;
import az.ingress.userms.model.dto.ChangePasswordDto;
import az.ingress.userms.model.dto.ResetPasswordDto;
import az.ingress.userms.model.entity.User;
import az.ingress.userms.model.entity.Verification;
import az.ingress.userms.model.enums.TokenType;
import az.ingress.userms.producer.KafkaProducer;
import az.ingress.userms.repository.UserRepository;
import az.ingress.userms.repository.VerificationRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static az.ingress.userms.model.enums.Exceptions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private VerificationRepository tokenRepository;
    @Mock private KafkaProducer kafkaProducer;
    @Mock private JwtService jwtService;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private PasswordServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "PASSWORD_RESET_EXPIRATION_TIME", 3600L);
        ReflectionTestUtils.setField(service, "RESET_PASSWORD_TOPIC", "reset-password");
    }

    @Test
    void changePassword() {
        String bearer = "Bearer REFRESH123";
        String email = "u@site.com";
        ChangePasswordDto dto = new ChangePasswordDto("old", "newP", "newP");

        User user = new User();
        user.setEmail(email);
        user.setPassword("ENC_OLD");

        when(jwtService.getUsername("REFRESH123")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);
        when(passwordEncoder.encode("newP")).thenReturn("ENC_NEW");

        service.changePassword(dto, bearer, response);

        assertThat(user.getPassword()).isEqualTo("ENC_NEW");
        verify(userRepository).save(same(user));
    }

    @Test
    void changePassword_whenOldPasswordWrong_throws() {
        String bearer = "Bearer XXX";
        String email = "u@site.com";
        ChangePasswordDto dto = new ChangePasswordDto("badOld", "n1", "n1");

        User user = new User();
        user.setEmail(email);
        user.setPassword("ENC");

        when(jwtService.getUsername("XXX")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("badOld", "ENC")).thenReturn(false);

        assertThatThrownBy(() -> service.changePassword(dto, bearer, response))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> assertThat(((ApplicationException) ex).getKey())
                        .isEqualTo("exception.password.is.not.correct"));

        verify(userRepository, never()).save(any());
    }
    @Test
    void changePassword_whenNewPasswordsMismatch_throws() {
        String bearer = "Bearer TTT";
        String email = "u@site.com";
        ChangePasswordDto dto = new ChangePasswordDto("old", "A", "B");

        User user = new User();
        user.setEmail(email);
        user.setPassword("ENC");

        when(jwtService.getUsername("TTT")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);

        assertThatThrownBy(() -> service.changePassword(dto, bearer, response))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> assertThat(((ApplicationException) ex).getKey())
                        .isEqualTo("exception.password.mismatch"));

        verify(userRepository, never()).save(any());
    }
    @Test
    void changePassword_whenUserMissing_doesNothing_currentImpl() {
        String bearer = "Bearer ZZZ";
        when(jwtService.getUsername("ZZZ")).thenReturn("missing@site.com");
        when(userRepository.findByEmail("missing@site.com")).thenReturn(Optional.empty());

        ChangePasswordDto dto = new ChangePasswordDto("x", "y", "y");
        service.changePassword(dto, bearer, response);

        verify(userRepository, never()).save(any());
    }

    @Test
    void forgetPassword() {
        String email = "u@site.com";
        User user = new User();
        user.setEmail(email);
        user.setFirstName("U");
        user.setLastName("Ser");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        ArgumentCaptor<UserResetPasswordDto> msgCaptor = ArgumentCaptor.forClass(UserResetPasswordDto.class);

        service.forgetPassword(email);

        verify(tokenRepository).save(argThat(v ->
                v.getUser() == user &&
                        v.getType() == TokenType.PASSWORD_RESET &&
                        Boolean.FALSE.equals(v.getIsUsed()) &&
                        Boolean.FALSE.equals(v.getIsExpired()) &&
                        v.getExpirationTime().isAfter(LocalDateTime.now())
        ));

        verify(kafkaProducer).sendPasswordResetMessage(eq("reset-password"), msgCaptor.capture());
        UserResetPasswordDto sent = msgCaptor.getValue();
        assertThat(sent.email()).isEqualTo(email);
        assertThat(sent.firstName()).isEqualTo("U");
        assertThat(sent.lastName()).isEqualTo("Ser");
        assertThat(sent.token()).isNotBlank();
    }
    @Test
    void forgetPassword_whenUserMissing_doesNothing() {
        when(userRepository.findByEmail("missing@site.com")).thenReturn(Optional.empty());

        service.forgetPassword("missing@site.com");

        verifyNoInteractions(tokenRepository, kafkaProducer);
    }

    @Test
    void setNewPassword() {
        String token = "TKN";
        User user = new User();
        user.setId(10L);
        user.setPassword("ENC_OLD");

        Verification ver = Verification.builder()
                .token(token)
                .user(user)
                .type(TokenType.PASSWORD_RESET)
                .isUsed(false)
                .isExpired(false)
                .expirationTime(LocalDateTime.now().plusMinutes(30))
                .build();

        ResetPasswordDto dto = new ResetPasswordDto("newP", "newP");

        when(tokenRepository.getValidToken(token, TokenType.PASSWORD_RESET.name()))
                .thenReturn(Optional.of(ver));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newP")).thenReturn("ENC_NEW");

        service.setNewPassword(token, dto);

        assertThat(user.getPassword()).isEqualTo("ENC_NEW");
        assertThat(ver.getIsExpired()).isFalse();

        verify(tokenRepository).save(same(ver));
        verify(userRepository).save(same(user));
    }
    @Test
    void setNewPassword_whenTokenMissing_throws() {
        when(tokenRepository.getValidToken("BAD", TokenType.PASSWORD_RESET.name()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setNewPassword("BAD", new ResetPasswordDto("a","a")))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> assertThat(((ApplicationException) ex).getKey())
                        .isEqualTo("exception.token.not.found"));

        verifyNoInteractions(userRepository, passwordEncoder, kafkaProducer);
    }
    @Test
    void setNewPassword_whenTokenExpired_throws() {
        String token = "EXP";
        User user = new User(); user.setId(1L);

        Verification expired = Verification.builder()
                .token(token)
                .user(user)
                .type(TokenType.PASSWORD_RESET)
                .isUsed(false)
                .isExpired(true)
                .expirationTime(LocalDateTime.now().minusMinutes(1))
                .build();

        when(tokenRepository.getValidToken(token, TokenType.PASSWORD_RESET.name()))
                .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.setNewPassword(token, new ResetPasswordDto("x","x")))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> assertThat(((ApplicationException) ex).getKey())
                        .isEqualTo("exception.token.expired"));

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }
    @Test
    void setNewPassword_whenUserNotFound_throwsNotFound() {
        String token = "T2";
        User user = new User(); user.setId(99L);

        Verification ver = Verification.builder()
                .token(token)
                .user(user)
                .type(TokenType.PASSWORD_RESET)
                .isUsed(false)
                .isExpired(false)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(tokenRepository.getValidToken(token, TokenType.PASSWORD_RESET.name()))
                .thenReturn(Optional.of(ver));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setNewPassword(token, new ResetPasswordDto("a","a")))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> assertThat(((ApplicationException) ex).getKey())
                        .isEqualTo("exception.not.found"));

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }
    @Test
    void setNewPassword_whenPasswordsMismatch_throws() {
        String token = "T3";
        User user = new User(); user.setId(1L);

        Verification ver = Verification.builder()
                .token(token)
                .user(user)
                .type(TokenType.PASSWORD_RESET)
                .isUsed(false)
                .isExpired(false)
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(tokenRepository.getValidToken(token, TokenType.PASSWORD_RESET.name()))
                .thenReturn(Optional.of(ver));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.setNewPassword(token, new ResetPasswordDto("a","b")))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> assertThat(((ApplicationException) ex).getKey())
                        .isEqualTo("exception.password.mismatch"));

        verify(userRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }
}