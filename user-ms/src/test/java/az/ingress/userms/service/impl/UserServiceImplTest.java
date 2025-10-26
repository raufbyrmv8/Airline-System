package az.ingress.userms.service.impl;

import az.ingress.common.model.exception.ApplicationException;
import az.ingress.userms.model.dto.response.UserResponseDto;
import az.ingress.userms.model.entity.User;
import az.ingress.userms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static az.ingress.userms.model.enums.Exceptions.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getUserDetailsById() {
        Long id = 10L;
        User u = new User();
        u.setId(id);
        u.setFirstName("Rauf");
        u.setLastName("Bayramov");
        u.setEmail("rauf@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(u));

        UserResponseDto dto = service.getUserDetailsById(id);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.name()).isEqualTo("Rauf");
        assertThat(dto.surname()).isEqualTo("Bayramov");
        assertThat(dto.email()).isEqualTo("rauf@example.com");

        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    void getUserDetailsById_whenMissing_throwsNotFound() {
        Long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserDetailsById(id))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void getUserDetailsByEmail() {
        String email = "user@site.com";
        User u = new User();
        u.setId(7L);
        u.setFirstName("User");
        u.setLastName("Test");
        u.setEmail(email);

        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, email, true, true))
                .thenReturn(Optional.of(u));

        UserResponseDto dto = service.getUserDetailsByEmail(email);

        assertThat(dto.id()).isEqualTo(7L);
        assertThat(dto.name()).isEqualTo("User");
        assertThat(dto.surname()).isEqualTo("Test");
        assertThat(dto.email()).isEqualTo(email);

        verify(userRepository).findByStatusAndEmailAndIsActiveAndIsEnabled(true, email, true, true);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserDetailsByEmail_whenMissing_throwsNotFound() {
        String email = "missing@site.com";
        when(userRepository.findByStatusAndEmailAndIsActiveAndIsEnabled(true, email, true, true))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserDetailsByEmail(email))
                .isInstanceOf(ApplicationException.class)
                .satisfies(ex -> {
                    ApplicationException ae = (ApplicationException) ex;
                    assertThat(ae.getKey()).isEqualTo("exception.not.found");
                });

        verify(userRepository).findByStatusAndEmailAndIsActiveAndIsEnabled(true, email, true, true);
        verifyNoMoreInteractions(userRepository);
    }
}