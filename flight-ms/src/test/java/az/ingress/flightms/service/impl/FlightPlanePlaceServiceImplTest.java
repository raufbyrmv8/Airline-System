package az.ingress.flightms.service.impl;

import az.ingress.flightms.exception.NotFoundException;
import az.ingress.flightms.mapper.FlightPlanePlaceMapper;
import az.ingress.flightms.model.dto.request.FlightPlanePlaceDto;
import az.ingress.flightms.model.entity.FlightPlanePlace;
import az.ingress.flightms.repository.FlightPlanePlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightPlanePlaceServiceImplTest {
    @Mock
    private FlightPlanePlaceRepository repository;

    @Mock
    private FlightPlanePlaceMapper mapper;

    private FlightPlanePlaceServiceImpl service;
    @BeforeEach
    void setUp() {
        service = new FlightPlanePlaceServiceImpl(repository, mapper);
    }
    @Test
    void update() {
        Long id = 10L;
        FlightPlanePlace entity = new FlightPlanePlace();
        FlightPlanePlaceDto dto = new FlightPlanePlaceDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        FlightPlanePlaceDto result = service.update(id, dto);

        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).updateEntity(entity, dto);

        verify(repository, never()).save(any());
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void update_whenEntityNotFound_throwsNotFound() {
        Long id = 99L;
        FlightPlanePlaceDto dto = new FlightPlanePlaceDto();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(id, dto));

        verify(repository, times(1)).findById(id);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getById() {
        Long id = 5L;
        FlightPlanePlace entity = new FlightPlanePlace();
        FlightPlanePlaceDto dto = new FlightPlanePlaceDto();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        FlightPlanePlaceDto result = service.getById(id);

        assertSame(dto, result);
        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).toDto(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getById_whenEntityNotFound_throwsNotFound() {
        Long id = 404L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(id));

        verify(repository, times(1)).findById(id);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
    @Test
    void getAll() {
        FlightPlanePlace e1 = new FlightPlanePlace();
        FlightPlanePlace e2 = new FlightPlanePlace();

        FlightPlanePlaceDto d1 = new FlightPlanePlaceDto();
        FlightPlanePlaceDto d2 = new FlightPlanePlaceDto();

        when(repository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.toDto(e1)).thenReturn(d1);
        when(mapper.toDto(e2)).thenReturn(d2);

        List<FlightPlanePlaceDto> result = service.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(d1, result.get(0));
        assertSame(d2, result.get(1));

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDto(e1);
        verify(mapper, times(1)).toDto(e2);
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void getAll_whenNoEntities_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<FlightPlanePlaceDto> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository, times(1)).findAll();
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void delete() {
        Long id = 7L;
        FlightPlanePlace entity = new FlightPlanePlace();
        entity.setStatus(true);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        service.delete(id);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(argThat(saved ->
                saved != null && Boolean.FALSE.equals(saved.getStatus())
        ));
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
    @Test
    void delete_whenEntityNotFound_throwsNotFound() {
        Long id = 404L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(id));

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
}