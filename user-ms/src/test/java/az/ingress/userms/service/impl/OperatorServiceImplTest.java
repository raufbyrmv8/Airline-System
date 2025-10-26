package az.ingress.userms.service.impl;

import az.ingress.common.kafka.OperatorRegisterDto;
import az.ingress.common.model.constant.Roles;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.userms.model.dto.request.OperatorRequestDto;
import az.ingress.userms.model.entity.User;
import az.ingress.userms.producer.KafkaProducer;
import az.ingress.userms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock
    private KafkaProducer kafkaProducer;
    @InjectMocks
    private OperatorServiceImpl service;
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "OPERATOR_REGISTER_TOPIC", "operator-register");
    }

    @Test
    void registerOperator() {
        OperatorRequestDto dto = new OperatorRequestDto("op@example.com");
        User user = new User();
        user.setEmail("op@example.com");
        user.setFirstName("Op");
        user.setLastName("Erator");

        List<String> admins = List.of("admin1@site.com", "admin2@site.com");

        when(userRepository.findByEmail("op@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findEmailsByRole(Roles.ADMIN)).thenReturn(Optional.of(admins));
        when(userRepository.save(same(user))).thenReturn(user);

        ArgumentCaptor<OperatorRegisterDto> msgCaptor = ArgumentCaptor.forClass(OperatorRegisterDto.class);

        // when
        service.registerOperator(dto);

        // then
        verify(userRepository).findByEmail("op@example.com");
        verify(userRepository).findEmailsByRole(Roles.ADMIN);
        verify(userRepository).save(same(user));

        verify(kafkaProducer).sendOperatorRegisterMessage(eq("operator-register"), msgCaptor.capture());
        OperatorRegisterDto sent = msgCaptor.getValue();
        assertThat(sent.email()).isEqualTo("op@example.com");
        assertThat(sent.firstName()).isEqualTo("Op");
        assertThat(sent.lastName()).isEqualTo("Erator");
        assertThat(sent.adminEmails()).containsExactlyElementsOf(admins);

        verifyNoMoreInteractions(userRepository, kafkaProducer);
    }

    @Test
    void registerOperator_whenUserMissing_throwsNotFound() {
        OperatorRequestDto dto = new OperatorRequestDto("missing@site.com");
        when(userRepository.findByEmail("missing@site.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registerOperator(dto))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findByEmail("missing@site.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(kafkaProducer);
    }
    @Test
    void registerOperator_whenAdminEmailsMissing_throwsNotFound_andNoKafka() {
        OperatorRequestDto dto = new OperatorRequestDto("op@example.com");
        User user = new User();
        user.setEmail("op@example.com");

        when(userRepository.findByEmail("op@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findEmailsByRole(Roles.ADMIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registerOperator(dto))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findByEmail("op@example.com");
        verify(userRepository).findEmailsByRole(Roles.ADMIN);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(kafkaProducer);
    }
    @Test
    void approvalOperator() {
        // given
        OperatorRequestDto dto = new OperatorRequestDto("op@gmail.com");
        User user = new User();
        user.setEmail("op@gmail.com");
        user.setRole(Roles.CUSTOMER);

        when(userRepository.findByEmail("op@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(same(user))).thenReturn(user);

        // when
        service.approvalOperator(dto);

        // then
        assertThat(user.getRole()).isEqualTo(Roles.OPERATOR);
        verify(userRepository).findByEmail("op@gmail.com");
        verify(userRepository).save(same(user));
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(kafkaProducer);
    }
    @Test
    void approvalOperator_whenUserMissing_throwsNotFound_andDoesNotSave() {
        OperatorRequestDto dto = new OperatorRequestDto("missing@site.com");
        when(userRepository.findByEmail("missing@site.com")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.approvalOperator(dto))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findByEmail("missing@site.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(kafkaProducer);
    }

    @Test
    void removeOperatorRole() {
        // given
        OperatorRequestDto dto = new OperatorRequestDto("op@example.com");
        User user = new User();
        user.setEmail("op@example.com");
        user.setRole(Roles.OPERATOR);

        when(userRepository.findByEmail("op@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(same(user))).thenReturn(user);

        // when
        service.removeOperatorRole(dto);

        // then
        assertThat(user.getRole()).isEqualTo(Roles.CUSTOMER);
        verify(userRepository).findByEmail("op@example.com");
        verify(userRepository).save(same(user));
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(kafkaProducer);
    }
    @Test
    void removeOperatorRole_whenUserMissing_throwsNotFound_andDoesNotSave() {
        // given
        OperatorRequestDto dto = new OperatorRequestDto("missing@site.com");
        when(userRepository.findByEmail("missing@site.com")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> service.removeOperatorRole(dto))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findByEmail("missing@site.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(kafkaProducer);
    }
}