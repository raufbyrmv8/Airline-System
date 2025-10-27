package az.ingress.flightms.service.impl;

import az.ingress.common.config.JwtSessionData;
import az.ingress.common.kafka.AdminNotificationDto;
import az.ingress.common.kafka.OperatorNotificationDto;
import az.ingress.flightms.config.client.UserClient;
import az.ingress.flightms.config.client.UserResponseDto;
import az.ingress.flightms.exception.NotFoundException;
import az.ingress.flightms.mapper.FlightMapper;
import az.ingress.flightms.model.dto.FlightDto;
import az.ingress.flightms.model.dto.FlightDtoByCreatedOperator;
import az.ingress.flightms.model.dto.request.FlightRequestDto;
import az.ingress.flightms.model.entity.Flight;
import az.ingress.flightms.model.entity.Plane;
import az.ingress.flightms.model.entity.PlanePlace;
import az.ingress.flightms.model.enums.ApprovalState;
import az.ingress.flightms.producer.KafkaProducer;
import az.ingress.flightms.repository.FlightRepository;
import az.ingress.flightms.repository.PlaneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {
    @Mock
    private FlightRepository flightRepository;
    @Mock private PlaneRepository planeRepository;
    @Mock private FlightMapper flightMapper;
    @Mock private JwtSessionData jwtSessionData;
    @Mock private UserClient userClient;
    @Mock private KafkaProducer kafkaProducer;

    private FlightServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FlightServiceImpl(
                flightRepository,
                planeRepository,
                flightMapper,
                jwtSessionData,
                userClient,
                kafkaProducer
        );
    }

    @Test
    void getById() {
        Long id = 123L;
        Flight entity = new Flight();
        entity.setId(id);

        FlightDto dto = new FlightDto();
        dto.setId(id);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));
        when(flightMapper.toDto(entity)).thenReturn(dto);

        FlightDto result = service.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(flightRepository, times(1)).findById(id);
        verify(flightMapper, times(1)).toDto(entity);

        verifyNoInteractions(planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }
    @Test
    void getById_whenEntityNotFound_throwsNotFound() {
        Long id = 404L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(id));

        verify(flightRepository, times(1)).findById(id);
        verifyNoInteractions(flightMapper, planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void getAll() {
        Flight e1 = new Flight(); e1.setId(1L);
        Flight e2 = new Flight(); e2.setId(2L);

        FlightDto d1 = new FlightDto(); d1.setId(1L);
        FlightDto d2 = new FlightDto(); d2.setId(2L);

        when(flightRepository.findAll()).thenReturn(List.of(e1, e2));
        when(flightMapper.toDto(e1)).thenReturn(d1);
        when(flightMapper.toDto(e2)).thenReturn(d2);

        List<FlightDto> result = service.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(flightRepository, times(1)).findAll();
        verify(flightMapper, times(1)).toDto(e1);
        verify(flightMapper, times(1)).toDto(e2);
        verifyNoInteractions(planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }
    @Test
    void getAll_whenNoEntities_returnsEmptyList() {
        when(flightRepository.findAll()).thenReturn(List.of());

        List<FlightDto> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(flightRepository, times(1)).findAll();
        verifyNoInteractions(flightMapper, planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void create() {
        ReflectionTestUtils.setField(service, "ADMIN_TOPIC", "admin-topic-test");
        Long currentUserId = 99L;
        when(jwtSessionData.getUserId()).thenReturn(currentUserId);

        FlightRequestDto req = new FlightRequestDto();
        req.setFrom("GYD");
        req.setTo("IST");
        req.setPrice(new BigDecimal("150.50"));
        req.setPlaneId(777L);

        Plane plane = new Plane();
        plane.setId(777L);
        plane.setCapacity(180);
        plane.setPlanePlaces(Set.of(new PlanePlace()));

        when(planeRepository.findByIdWithPlanePlaces(777L)).thenReturn(Optional.of(plane));

        Flight toSave = new Flight();
        when(flightMapper.toEntity(req)).thenReturn(toSave);

        Flight saved = new Flight();
        saved.setId(123L);
        saved.setPlane(plane);
        saved.setTicketCount(180);
        saved.setApprovalState(ApprovalState.PENDING);
        when(flightRepository.save(toSave)).thenReturn(saved);

        FlightDto expectedDto = new FlightDto();
        expectedDto.setId(123L);
        when(flightMapper.toDto(saved)).thenReturn(expectedDto);

        FlightDto result = service.create(req);

        assertNotNull(result);
        assertEquals(123L, result.getId());

        assertSame(plane, toSave.getPlane(), "Plane set on entity");
        assertEquals(180, toSave.getTicketCount(), "TicketCount equals plane capacity");
        assertEquals(ApprovalState.PENDING, toSave.getApprovalState(), "ApprovalState=PENDING");

        verify(planeRepository, times(1)).findByIdWithPlanePlaces(777L);
        verify(flightMapper, times(1)).toEntity(req);
        verify(flightRepository, times(1)).save(toSave);
        verify(flightMapper, times(1)).toDto(saved);
        verify(jwtSessionData, times(1)).getUserId();

        ArgumentCaptor<AdminNotificationDto> captor = ArgumentCaptor.forClass(AdminNotificationDto.class);
        verify(kafkaProducer, times(1))
                .notifyAdminForApprovement(eq("admin-topic-test"), captor.capture());

        AdminNotificationDto sent = captor.getValue();
        assertEquals(saved.getId(), sent.flightId());

        verifyNoMoreInteractions(flightRepository, planeRepository, flightMapper, kafkaProducer, userClient, jwtSessionData);
    }
    @Test
    void create_whenPlaneNotFound_throwsNotFound() {
        FlightRequestDto req = new FlightRequestDto();
        req.setPlaneId(111L);
        when(planeRepository.findByIdWithPlanePlaces(111L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(req));

        verify(planeRepository, times(1)).findByIdWithPlanePlaces(111L);
        verifyNoInteractions(flightRepository, flightMapper, kafkaProducer, userClient, jwtSessionData);
    }

    @Test
    void create_whenPlanePlacesEmpty_throwsNotFound() {
        FlightRequestDto req = new FlightRequestDto();
        req.setPlaneId(222L);

        Plane plane = new Plane();
        plane.setId(222L);
        plane.setCapacity(150);
        plane.setPlanePlaces(Set.of());

        when(planeRepository.findByIdWithPlanePlaces(222L)).thenReturn(Optional.of(plane));

        assertThrows(NotFoundException.class, () -> service.create(req));

        verify(planeRepository, times(1)).findByIdWithPlanePlaces(222L);
        verifyNoInteractions(flightRepository, flightMapper, kafkaProducer, userClient, jwtSessionData);
    }

    @Test
    void update() {
        Long id = 55L;

        FlightRequestDto req = new FlightRequestDto();
        req.setFrom("GYD");
        req.setTo("IST");

        Flight entity = new Flight();
        entity.setId(id);

        FlightDto mapped = new FlightDto();
        mapped.setId(id);

        when(flightRepository.findById(id)).thenReturn(java.util.Optional.of(entity));
        when(flightMapper.toDto(entity)).thenReturn(mapped);

        FlightDto result = service.update(id, req);

        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(flightRepository, times(1)).findById(id);
        verify(flightMapper, times(1)).updateFlight(entity, req);
        verify(flightMapper, times(1)).toDto(entity);

        verify(flightRepository, never()).save(any());

        verifyNoInteractions(planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }
    @Test
    void update_whenEntityNotFound_throwsNotFound() {
        Long id = 404L;
        FlightRequestDto req = new FlightRequestDto();
        when(flightRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(id, req));

        verify(flightRepository, times(1)).findById(id);
        verifyNoInteractions(flightMapper, planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void delete() {
        Long id = 77L;
        Flight entity = new Flight();
        entity.setId(id);
        entity.setStatus(true);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        service.delete(id);

        verify(flightRepository, times(1)).findById(id);

        ArgumentCaptor<Flight> captor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository, times(1)).save(captor.capture());

        Flight saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(id, saved.getId());
        assertFalse(saved.getStatus(), "status must be set to false");

        verifyNoInteractions(planeRepository, flightMapper, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void delete_whenEntityNotFound_throwsNotFound_andDoesNotSave() {
        Long id = 404L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(id));

        verify(flightRepository, times(1)).findById(id);
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(planeRepository, flightMapper, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository);
    }
    @Test
    void approveFlight() {
        ReflectionTestUtils.setField(service, "OPERATOR_TOPIC", "operator-topic-test");

        Long id = 10L;
        Long createdBy = 900L;
        String feedback = "Looks good";
        Flight entity = new Flight();
        entity.setId(id);
        entity.setCreatedBy(createdBy);
        entity.setApprovalState(ApprovalState.PENDING);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        UserResponseDto operator = new UserResponseDto(createdBy, "Ali", "Veli", "ali@example.com");
        when(userClient.getUserDetailsById(createdBy)).thenReturn(operator);

        service.approveFlight(id, feedback);

        verify(flightRepository, times(1)).findById(id);

        ArgumentCaptor<Flight> flightCaptor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository, times(1)).save(flightCaptor.capture());
        Flight saved = flightCaptor.getValue();

        assertEquals(ApprovalState.APPROVED, saved.getApprovalState());
        assertEquals(feedback, saved.getFeedbackMessage());

        ArgumentCaptor<OperatorNotificationDto> notifCaptor = ArgumentCaptor.forClass(OperatorNotificationDto.class);
        verify(kafkaProducer, times(1))
                .notifyOperator(eq("operator-topic-test"), notifCaptor.capture());

        OperatorNotificationDto sent = notifCaptor.getValue();
        assertEquals(id, sent.flightId());
        assertEquals(operator.email(), sent.operatorEmail());
        assertEquals(operator.name(), sent.operatorName());
        assertEquals(operator.surname(), sent.operatorSurname());
        assertEquals(String.valueOf(ApprovalState.APPROVED), sent.approvalState());
        assertEquals("Looks good", sent.feedbackMessage());

        verifyNoMoreInteractions(flightRepository, kafkaProducer, userClient);
        verifyNoInteractions(planeRepository, flightMapper, jwtSessionData);
    }
    @Test
    void approveFlight_whenPendingAndFeedbackNull_usesDefaultFeedback() {
        // given
        ReflectionTestUtils.setField(service, "OPERATOR_TOPIC", "operator-topic-test");

        Long id = 11L;
        Long createdBy = 901L;
        Flight entity = new Flight();
        entity.setId(id);
        entity.setCreatedBy(createdBy);
        entity.setApprovalState(ApprovalState.PENDING);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        UserResponseDto operator = new UserResponseDto(createdBy, "Aynur", "Quliyeva", "aynur@example.com");
        when(userClient.getUserDetailsById(createdBy)).thenReturn(operator);

        service.approveFlight(id, null);

        ArgumentCaptor<Flight> flightCaptor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository).save(flightCaptor.capture());
        assertEquals(ApprovalState.APPROVED, flightCaptor.getValue().getApprovalState());
        assertNull(flightCaptor.getValue().getFeedbackMessage());

        ArgumentCaptor<OperatorNotificationDto> notifCaptor = ArgumentCaptor.forClass(OperatorNotificationDto.class);
        verify(kafkaProducer).notifyOperator(eq("operator-topic-test"), notifCaptor.capture());
        assertEquals("No feedback provided", notifCaptor.getValue().feedbackMessage());

        verifyNoMoreInteractions(flightRepository, kafkaProducer, userClient);
        verifyNoInteractions(planeRepository, flightMapper, jwtSessionData);
    }

    @Test
    void approveFlight_whenNotFound_throwsNotFound() {
        Long id = 404L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.approveFlight(id, "x"));

        verify(flightRepository, times(1)).findById(id);
        verifyNoInteractions(userClient, kafkaProducer, planeRepository, flightMapper, jwtSessionData);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void approveFlight_whenStateIsNotPending_throwsIllegalState() {
        Long id = 12L;
        Flight entity = new Flight();
        entity.setId(id);
        entity.setApprovalState(ApprovalState.APPROVED);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.approveFlight(id, "ok"));

        verify(flightRepository, times(1)).findById(id);
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(userClient, kafkaProducer, planeRepository, flightMapper, jwtSessionData);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void rejectFlight() {
        ReflectionTestUtils.setField(service, "OPERATOR_TOPIC", "operator-topic-test");
        Long id = 20L;
        Long createdBy = 700L;
        String feedback = "Incorrect schedule";

        Flight entity = new Flight();
        entity.setId(id);
        entity.setCreatedBy(createdBy);
        entity.setApprovalState(ApprovalState.PENDING);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        UserResponseDto operator = new UserResponseDto(createdBy, "Nigar", "Aliyeva", "nigar@example.com");
        when(userClient.getUserDetailsById(createdBy)).thenReturn(operator);

        service.rejectFlight(id, feedback);

        ArgumentCaptor<Flight> flightCaptor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository).save(flightCaptor.capture());
        Flight saved = flightCaptor.getValue();
        assertEquals(ApprovalState.REJECTED, saved.getApprovalState());
        assertEquals(feedback, saved.getFeedbackMessage());

        ArgumentCaptor<OperatorNotificationDto> notifCaptor = ArgumentCaptor.forClass(OperatorNotificationDto.class);
        verify(kafkaProducer).notifyOperator(eq("operator-topic-test"), notifCaptor.capture());
        OperatorNotificationDto sent = notifCaptor.getValue();
        assertEquals(id, sent.flightId());
        assertEquals("nigar@example.com", sent.operatorEmail());
        assertEquals("Nigar", sent.operatorName());
        assertEquals("Aliyeva", sent.operatorSurname());
        assertEquals(String.valueOf(ApprovalState.REJECTED), sent.approvalState());
        assertEquals("Incorrect schedule", sent.feedbackMessage());

        verify(flightRepository).findById(id);
        verifyNoMoreInteractions(flightRepository, kafkaProducer, userClient);
        verifyNoInteractions(planeRepository, flightMapper, jwtSessionData);
    }

    @Test
    void rejectFlight_whenPendingAndFeedbackNull_usesDefaultFeedbackInNotification() {
        ReflectionTestUtils.setField(service, "OPERATOR_TOPIC", "operator-topic-test");
        Long id = 21L;
        Long createdBy = 701L;

        Flight entity = new Flight();
        entity.setId(id);
        entity.setCreatedBy(createdBy);
        entity.setApprovalState(ApprovalState.PENDING);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        UserResponseDto operator = new UserResponseDto(createdBy, "Ramil", "Qasımov", "ramil@example.com");
        when(userClient.getUserDetailsById(createdBy)).thenReturn(operator);

        service.rejectFlight(id, null);

        ArgumentCaptor<Flight> flightCaptor = ArgumentCaptor.forClass(Flight.class);
        verify(flightRepository).save(flightCaptor.capture());
        assertEquals(ApprovalState.REJECTED, flightCaptor.getValue().getApprovalState());
        assertNull(flightCaptor.getValue().getFeedbackMessage());

        ArgumentCaptor<OperatorNotificationDto> notifCaptor = ArgumentCaptor.forClass(OperatorNotificationDto.class);
        verify(kafkaProducer).notifyOperator(eq("operator-topic-test"), notifCaptor.capture());
        assertEquals("No feedback provided", notifCaptor.getValue().feedbackMessage());

        verifyNoMoreInteractions(flightRepository, kafkaProducer, userClient);
        verifyNoInteractions(planeRepository, flightMapper, jwtSessionData);
    }

    @Test
    void rejectFlight_whenNotFound_throwsNotFound() {
        Long id = 404L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.rejectFlight(id, "x"));

        verify(flightRepository).findById(id);
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(userClient, kafkaProducer, planeRepository, flightMapper, jwtSessionData);
    }

    @Test
    void rejectFlight_whenStateIsNotPending_throwsIllegalState() {
        Long id = 22L;
        Flight entity = new Flight();
        entity.setId(id);
        entity.setApprovalState(ApprovalState.APPROVED);

        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.rejectFlight(id, "reason"));

        verify(flightRepository).findById(id);
        verify(flightRepository, never()).save(any());
        verifyNoInteractions(userClient, kafkaProducer, planeRepository, flightMapper, jwtSessionData);
    }

    @Test
    void getPendingFlightsWithOperatorDetails() {
        Flight f1 = new Flight(); f1.setId(1L); f1.setCreatedBy(100L); f1.setApprovalState(ApprovalState.PENDING);
        Flight f2 = new Flight(); f2.setId(2L); f2.setCreatedBy(100L); f2.setApprovalState(ApprovalState.PENDING);
        Flight f3 = new Flight(); f3.setId(3L); f3.setCreatedBy(101L); f3.setApprovalState(ApprovalState.PENDING);

        when(flightRepository.findByApprovalState(ApprovalState.PENDING))
                .thenReturn(java.util.List.of(f1, f2, f3));

        FlightDto d1 = new FlightDto(); d1.setId(1L);
        FlightDto d2 = new FlightDto(); d2.setId(2L);
        FlightDto d3 = new FlightDto(); d3.setId(3L);
        when(flightMapper.toDto(f1)).thenReturn(d1);
        when(flightMapper.toDto(f2)).thenReturn(d2);
        when(flightMapper.toDto(f3)).thenReturn(d3);

        UserResponseDto op100 = new UserResponseDto(100L, "Ali", "Veli", "ali@example.com");
        UserResponseDto op101 = new UserResponseDto(101L, "Aynur", "Quliyeva", "aynur@example.com");
        when(userClient.getUserDetailsById(100L)).thenReturn(op100);
        when(userClient.getUserDetailsById(101L)).thenReturn(op101);

        java.util.List<FlightDtoByCreatedOperator> result = service.getPendingFlightsWithOperatorDetails();

        assertNotNull(result);
        assertEquals(3, result.size());

        FlightDtoByCreatedOperator r1 = result.get(0);
        FlightDtoByCreatedOperator r2 = result.get(1);
        FlightDtoByCreatedOperator r3 = result.get(2);

        assertEquals(100L, r1.getOperatorId());
        assertEquals("Ali", r1.getOperatorName());
        assertEquals("Veli", r1.getOperatorSurname());
        assertEquals("ali@example.com", r1.getOperatorEmail());
        assertSame(d1, r1.getFlightDto());

        assertEquals(100L, r2.getOperatorId());
        assertEquals("Ali", r2.getOperatorName());
        assertEquals("Veli", r2.getOperatorSurname());
        assertEquals("ali@example.com", r2.getOperatorEmail());
        assertSame(d2, r2.getFlightDto());

        assertEquals(101L, r3.getOperatorId());
        assertEquals("Aynur", r3.getOperatorName());
        assertEquals("Quliyeva", r3.getOperatorSurname());
        assertEquals("aynur@example.com", r3.getOperatorEmail());
        assertSame(d3, r3.getFlightDto());

        verify(flightRepository, times(1)).findByApprovalState(ApprovalState.PENDING);
        verify(flightMapper, times(1)).toDto(f1);
        verify(flightMapper, times(1)).toDto(f2);
        verify(flightMapper, times(1)).toDto(f3);

        verify(userClient, times(1)).getUserDetailsById(100L);
        verify(userClient, times(1)).getUserDetailsById(101L);

        verifyNoMoreInteractions(flightRepository, flightMapper, userClient);
        verifyNoInteractions(planeRepository, kafkaProducer, jwtSessionData);
    }
    @Test
    void getPendingFlightsWithOperatorDetails_whenEmpty_returnsEmptyList_andNoExtraCalls() {
        when(flightRepository.findByApprovalState(ApprovalState.PENDING))
                .thenReturn(java.util.List.of());

        List<FlightDtoByCreatedOperator> result = service.getPendingFlightsWithOperatorDetails();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(flightRepository, times(1)).findByApprovalState(ApprovalState.PENDING);
        verifyNoInteractions(flightMapper, userClient, planeRepository, kafkaProducer, jwtSessionData);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void getFlightsByState() {
        String state = "approved";
        Flight e1 = new Flight(); e1.setId(1L);
        Flight e2 = new Flight(); e2.setId(2L);

        FlightDto d1 = new FlightDto(); d1.setId(1L);
        FlightDto d2 = new FlightDto(); d2.setId(2L);

        when(flightRepository.findByApprovalState(ApprovalState.APPROVED))
                .thenReturn(java.util.List.of(e1, e2));
        when(flightMapper.toDto(e1)).thenReturn(d1);
        when(flightMapper.toDto(e2)).thenReturn(d2);

        var result = service.getFlightsByState(state);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(flightRepository, times(1)).findByApprovalState(ApprovalState.APPROVED);
        verify(flightMapper, times(1)).toDto(e1);
        verify(flightMapper, times(1)).toDto(e2);
        verifyNoInteractions(planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository, flightMapper);
    }
    @Test
    void getFlightsByState_whenEmpty_returnsEmptyList() {
        String state = "REJECTED";
        when(flightRepository.findByApprovalState(ApprovalState.REJECTED))
                .thenReturn(java.util.List.of());

        var result = service.getFlightsByState(state);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(flightRepository, times(1)).findByApprovalState(ApprovalState.REJECTED);
        verifyNoInteractions(flightMapper, planeRepository, jwtSessionData, userClient, kafkaProducer);
        verifyNoMoreInteractions(flightRepository);
    }

    @Test
    void getFlightsByState_whenInvalidState_throwsIllegalArgument() {
        String state = "WRONG_STATE";

        assertThrows(IllegalArgumentException.class, () -> service.getFlightsByState(state));

        verifyNoInteractions(flightRepository, flightMapper, planeRepository, jwtSessionData, userClient, kafkaProducer);
    }
}