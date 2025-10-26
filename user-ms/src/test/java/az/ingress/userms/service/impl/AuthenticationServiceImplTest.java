package az.ingress.userms.service.impl;

import az.ingress.common.kafka.UserRegisterDto;
import az.ingress.common.model.constant.Roles;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.common.service.JwtService;
import az.ingress.userms.mapper.UserMapper;
import az.ingress.userms.model.dto.request.LoginRequestDto;
import az.ingress.userms.model.dto.request.UserRequestDto;
import az.ingress.userms.model.dto.response.TokenResponseDto;
import az.ingress.userms.model.entity.User;
import az.ingress.userms.model.enums.TokenType;
import az.ingress.userms.producer.KafkaProducer;
import az.ingress.userms.repository.UserRepository;
import az.ingress.userms.service.VerificationService;
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

import java.util.Map;
import java.util.Optional;

import static az.ingress.userms.model.enums.Exceptions.BAD_CREDENTIALS;
import static az.ingress.userms.model.enums.Exceptions.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock
    private VerificationService verificationService;
    @Mock private JwtService jwtService;
    @Mock private KafkaProducer kafkaProducer;
    @Mock private HttpServletResponse response;
    @InjectMocks
    private AuthenticationServiceImpl service;
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "USER_REGISTRATION_TOPIC", "user-registration");
    }

    @Test
    void createUser() {
        // given
        UserRequestDto req = new UserRequestDto("rauf@gmail.com", "12345", "Rauf", "Bayramov");

        User toSave = new User();
        toSave.setEmail("rauf@gmail.com");
        toSave.setPassword("12345");
        toSave.setFirstName("Rauf");
        toSave.setLastName("Bayramov");

        when(userRepository.findByEmail("rauf@gmail.com")).thenReturn(Optional.empty());
        when(userMapper.map(req)).thenReturn(toSave);
        when(passwordEncoder.encode("12345")).thenReturn("ENC");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(100L);
            return u;
        });

        when(verificationService.sendVerificationEmail(any(User.class)))
                .thenReturn("reg-token-123");

        ArgumentCaptor<UserRegisterDto> dtoCaptor = ArgumentCaptor.forClass(UserRegisterDto.class);

        service.createUser(req);

        assertThat(toSave.getPassword()).isEqualTo("ENC");
        assertThat(toSave.getId()).isEqualTo(100L);

        verify(userRepository).findByEmail("rauf@gmail.com");
        verify(userMapper).map(req);
        verify(userRepository).save(toSave);

        verify(verificationService).sendVerificationEmail(same(toSave));

        verify(kafkaProducer).sendUserRegistration(eq("user-registration"), dtoCaptor.capture());

        UserRegisterDto sent = dtoCaptor.getValue();
        assertThat(sent.email()).isEqualTo("rauf@gmail.com");
        assertThat(sent.firstName()).isEqualTo("Rauf");
        assertThat(sent.lastName()).isEqualTo("Bayramov");
        assertThat(sent.token()).isEqualTo("reg-token-123");

        verifyNoMoreInteractions(userRepository, userMapper, verificationService, kafkaProducer);
    }

    @Test
    void verifyUser() {
        String token = "valid-token-123";
        User user = new User();
        user.setId(10L);
        user.setIsEnabled(false);

        when(verificationService.getUserByValidToken(eq(token), eq(TokenType.REGISTRATION)))
                .thenReturn(user);

        service.verifyUser(token);

        assertThat(user.getIsEnabled()).isTrue();
        verify(verificationService).getUserByValidToken(token, TokenType.REGISTRATION);
        verify(userRepository).save(same(user));
        verifyNoMoreInteractions(userRepository, verificationService);
        verifyNoInteractions(jwtService, kafkaProducer, userMapper, passwordEncoder);
    }
    @Test
    void verifyUser_whenTokenInvalid_shouldThrow_andNotSave() {
        String token = "token";
        when(verificationService.getUserByValidToken(eq(token), eq(TokenType.REGISTRATION)))
                .thenThrow(new ApplicationException(NOT_FOUND, token));

        assertThatThrownBy(() -> service.verifyUser(token))
                .isInstanceOf(ApplicationException.class);

        verify(verificationService).getUserByValidToken(token, TokenType.REGISTRATION);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtService, kafkaProducer, userMapper, passwordEncoder);
    }

    @Test
    void login() {
        // given
        String username = "rauf@gmail.com";
        String rawPass = "12345";
        LoginRequestDto dto = new LoginRequestDto(username, rawPass);

        User user = new User();
        user.setId(7L);
        user.setEmail(username);
        user.setRole(Roles.CUSTOMER);

        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, username, true, true))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPass, user.getPassword())).thenReturn(true);

        when(jwtService.generateToken(eq(username), any(Map.class)))
                .thenReturn("ACCESS_TOKEN");
        when(jwtService.generateRefresh(eq(username), any(Map.class)))
                .thenReturn("REFRESH_TOKEN");

        TokenResponseDto res = service.login(dto);

        assertThat(res.token()).isEqualTo("ACCESS_TOKEN");
        assertThat(res.refreshToken()).isEqualTo("REFRESH_TOKEN");

        verify(userRepository).findByStatusAndEmailAndIsActiveAndIsEnabled(true, username, true, true);
        verify(passwordEncoder).matches(rawPass, user.getPassword());
        verify(jwtService).generateToken(eq(username), any(Map.class));
        verify(jwtService).generateRefresh(eq(username), any(Map.class));
        verifyNoMoreInteractions(jwtService, userRepository, passwordEncoder);
    }
    @Test
    void login_withBadPassword_throwsBadCredentials() {
        // given
        String username = "rauf@gmail.com";
        LoginRequestDto dto = new LoginRequestDto(username, "wrong");

        User user = new User();
        user.setEmail(username);
        user.setPassword("ENC");
        user.setRole(Roles.CUSTOMER);

        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, username, true, true))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENC")).thenReturn(false);

        assertThatThrownBy(() -> service.login(dto))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.bad.credentials");
                });

        verify(passwordEncoder).matches("wrong", "ENC");
        verify(jwtService, never()).generateToken(anyString(), anyMap());
        verify(jwtService, never()).generateRefresh(anyString(), anyMap());
    }
    @Test
    void login_whenUserNotFound_throwsNotFound() {
        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, "x@x", true, true))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequestDto("x@x", "p")))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findByStatusAndEmailAndIsActiveAndIsEnabled(true, "x@x", true, true);
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void refresh() {
        String oldRefresh = "REFRESH_OLD";
        String username = "rauf@gmail.com";

        User user = new User();
        user.setId(2L);
        user.setEmail(username);
        user.setRole(Roles.CUSTOMER);

        when(jwtService.getUsername(oldRefresh)).thenReturn(username);
        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, username, true, true))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(eq(username), anyMap())).thenReturn("ACCESS_NEW");
        when(jwtService.generateRefresh(eq(username), anyMap())).thenReturn("REFRESH_NEW");

        TokenResponseDto res = service.refresh(oldRefresh);

        assertThat(res.token()).isEqualTo("ACCESS_NEW");
        assertThat(res.refreshToken()).isEqualTo("REFRESH_NEW");

        verify(jwtService).getUsername(oldRefresh);
        verify(userRepository).findByStatusAndEmailAndIsActiveAndIsEnabled(true, username, true, true);
        verify(jwtService).generateToken(eq(username), anyMap());
        verify(jwtService).generateRefresh(eq(username), anyMap());
        verifyNoMoreInteractions(jwtService, userRepository);
    }
    @Test
    void refresh_withInvalidRefreshToken_throws() {
        String badRefresh = "BAD_REFRESH";
        when(jwtService.getUsername(badRefresh))
                .thenThrow(new ApplicationException(NOT_FOUND,badRefresh));

        assertThatThrownBy(() -> service.refresh(badRefresh))
                .isInstanceOf(ApplicationException.class);

        verify(jwtService).getUsername(badRefresh);
        verifyNoInteractions(userRepository);
        verify(jwtService, never()).generateToken(anyString(), anyMap());
        verify(jwtService, never()).generateRefresh(anyString(), anyMap());
    }
    @Test
    void refresh_whenUserNotFound_throwsNotFound() {
        String refresh = "R";
        when(jwtService.getUsername(refresh)).thenReturn("missing@gmail.com");
        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, "missing@gmail.com", true, true))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh(refresh))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(jwtService).getUsername(refresh);
        verify(userRepository).findByStatusAndEmailAndIsActiveAndIsEnabled(true, "missing@gmail.com", true, true);
        verify(jwtService, never()).generateToken(anyString(), anyMap());
        verify(jwtService, never()).generateRefresh(anyString(), anyMap());
    }
}