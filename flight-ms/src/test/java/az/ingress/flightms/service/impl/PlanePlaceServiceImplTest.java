package az.ingress.flightms.service.impl;

import az.ingress.flightms.mapper.PlanePlaceMapper;
import az.ingress.flightms.model.dto.request.PlanePlaceRequest;
import az.ingress.flightms.model.dto.response.PlanePlaceResponse;
import az.ingress.flightms.model.enums.PlaceType;
import az.ingress.flightms.repository.PlanePlaceRepository;
import az.ingress.flightms.service.PlanePlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import az.ingress.flightms.mapper.PlanePlaceMapper;           // sizdəki real package
import az.ingress.flightms.model.entity.PlanePlace;            // sizdəki real package
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanePlaceServiceImplTest {

    @Mock private PlanePlaceRepository repository;
    @Mock
    private PlanePlaceMapper mapper;

    private PlanePlaceService service;

    @BeforeEach
    void setUp() {
        service = new PlanePlaceServiceImpl(repository, mapper);
    }

    @Test
    void createPlanePlace() {
        PlanePlaceRequest req = new PlanePlaceRequest();
        req.setPlace(1);
        req.setRow(1);
        req.setPlaceNumber(12);
        req.setPlaceType(PlaceType.BUSINESS);

        PlanePlace entityToSave = new PlanePlace();
        when(mapper.mapToPlanePlace(any(PlanePlaceResponse.class))).thenReturn(entityToSave);

        PlanePlace saved = new PlanePlace();
        saved.setId(100L);
        when(repository.save(entityToSave)).thenReturn(saved);

        PlanePlaceResponse expected = PlanePlaceResponse.builder()
                .id(100L)
                .place(1)
                .row(1)
                .placeNumber(12)
                .placeType(PlaceType.BUSINESS)
                .build();
        when(mapper.mapToPlanePlaceToResponse(saved)).thenReturn(expected);

        PlanePlaceResponse result = service.createPlanePlace(req);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1, result.getPlace());
        assertEquals(1, result.getRow());
        assertEquals(12, result.getPlaceNumber());
        assertEquals(PlaceType.BUSINESS, result.getPlaceType());

        ArgumentCaptor<PlanePlaceResponse> respCaptor = ArgumentCaptor.forClass(PlanePlaceResponse.class);
        verify(mapper).mapToPlanePlace(respCaptor.capture());
        PlanePlaceResponse builtInside = respCaptor.getValue();
        assertEquals(1, builtInside.getPlace());
        assertEquals(1, builtInside.getRow());
        assertEquals(12, builtInside.getPlaceNumber());
        assertEquals(PlaceType.BUSINESS, builtInside.getPlaceType());

        verify(repository, times(1)).save(entityToSave);
        verify(mapper, times(1)).mapToPlanePlaceToResponse(saved);
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void createPlanePlace_whenPlaceNumberIsNull_throwsIllegalArgument() {
        PlanePlaceRequest req = new PlanePlaceRequest();
        req.setPlace(1);
        req.setRow(2);
        req.setPlaceNumber(null);
        req.setPlaceType(PlaceType.BUSINESS);

        assertThrows(IllegalArgumentException.class, () -> service.createPlanePlace(req));

        verifyNoInteractions(repository, mapper);
    }

    @Test
    void createPlanePlace_whenPlaceTypeIsNull_throwsIllegalArgument() {
        PlanePlaceRequest req = new PlanePlaceRequest();
        req.setPlace(1);
        req.setRow(3);
        req.setPlaceNumber(5);
        req.setPlaceType(null);

        assertThrows(IllegalArgumentException.class, () -> service.createPlanePlace(req));

        verifyNoInteractions(repository, mapper);
    }

    @Test
    void getPlanePlaceById() {
        Long id = 10L;
        PlanePlace entity = new PlanePlace();
        entity.setId(id);

        PlanePlaceResponse mapped = PlanePlaceResponse.builder()
                .id(id)
                .place(1)
                .row(1)
                .placeNumber(12)
                .placeType(PlaceType.BUSINESS)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.mapToPlanePlaceToResponse(entity)).thenReturn(mapped);

        PlanePlaceResponse result = service.getPlanePlaceById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(1, result.getPlace());
        assertEquals(1, result.getRow());
        assertEquals(12, result.getPlaceNumber());
        assertEquals(PlaceType.BUSINESS, result.getPlaceType());

        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).mapToPlanePlaceToResponse(entity);
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void getPlanePlaceById_whenNotFound_throwsRuntimeException() {
        Long id = 404L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getPlanePlaceById(id));
        verify(repository, times(1)).findById(id);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAllPlanePlaces() {
        PlanePlace e1 = new PlanePlace(); e1.setId(1L);
        PlanePlace e2 = new PlanePlace(); e2.setId(2L);

        PlanePlaceResponse r1 = PlanePlaceResponse.builder()
                .id(1L).place(1).row(1).placeNumber(10).placeType(PlaceType.BUSINESS).build();
        PlanePlaceResponse r2 = PlanePlaceResponse.builder()
                .id(2L).place(1).row(2).placeNumber(12).placeType(PlaceType.BUSINESS).build();

        when(repository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.mapToPlanePlaceToResponse(e1)).thenReturn(r1);
        when(mapper.mapToPlanePlaceToResponse(e2)).thenReturn(r2);

        Set<PlanePlaceResponse> result = service.getAllPlanePlaces();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertTrue(result.contains(r1));
        assertTrue(result.contains(r2));

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).mapToPlanePlaceToResponse(e1);
        verify(mapper, times(1)).mapToPlanePlaceToResponse(e2);
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void getAllPlanePlaces_whenEmpty_returnsEmptySet() {
        when(repository.findAll()).thenReturn(List.of());

        Set<PlanePlaceResponse> result = service.getAllPlanePlaces();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository, times(1)).findAll();
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updatePlanePlace() {
        Long id = 15L;

        PlanePlaceRequest req = new PlanePlaceRequest();
        req.setPlace(1);
        req.setRow(4);
        req.setPlaceNumber(22);
        req.setPlaceType(PlaceType.BUSINESS);

        PlanePlace entity = new PlanePlace();
        entity.setId(id);
        entity.setPlace(1);
        entity.setRow(1);
        entity.setPlaceNumber(1);
        entity.setPlaceType(PlaceType.BUSINESS);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        PlanePlaceResponse mapped = PlanePlaceResponse.builder()
                .id(id).place(1).row(4).placeNumber(22).placeType(PlaceType.BUSINESS).build();
        when(mapper.mapToPlanePlaceToResponse(entity)).thenReturn(mapped);

        PlanePlaceResponse result = service.updatePlanePlace(id, req);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(1, result.getPlace());
        assertEquals(4, result.getRow());
        assertEquals(22, result.getPlaceNumber());
        assertEquals(PlaceType.BUSINESS, result.getPlaceType());


        assertEquals(1, entity.getPlace());
        assertEquals(4, entity.getRow());
        assertEquals(22, entity.getPlaceNumber());
        assertEquals(PlaceType.BUSINESS, entity.getPlaceType());


        ArgumentCaptor<PlanePlace> captor = ArgumentCaptor.forClass(PlanePlace.class);
        verify(repository).save(captor.capture());
        assertSame(entity, captor.getValue());

        verify(repository).findById(id);
        verify(mapper).mapToPlanePlaceToResponse(entity);
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void updatePlanePlace_whenNotFound_throwsRuntimeException_andDoesNotSave() {
        Long id = 404L;
        PlanePlaceRequest req = new PlanePlaceRequest();
        when(repository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.updatePlanePlace(id, req));

        verify(repository).findById(id);
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
    @Test
    void deletePlanePlace() {
        Long id = 30L;
        PlanePlace entity = new PlanePlace();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        service.deletePlanePlace(id);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).delete(entity);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
    @Test
    void deletePlanePlace_whenNotFound_doesNothing() {
        Long id = 404L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        service.deletePlanePlace(id);

        verify(repository, times(1)).findById(id);
        verify(repository, never()).delete(any());
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
    @Test
    void getPlanePlacesByType() {
        String type = "BUSINESS";
        PlanePlace entity = new PlanePlace();
        entity.setId(11L);

        PlanePlaceResponse mapped = PlanePlaceResponse.builder()
                .id(11L).place(1).row(1).placeNumber(5).placeType(PlaceType.BUSINESS).build();

        when(repository.findByPlaceType(PlaceType.BUSINESS)).thenReturn(Optional.of(entity));
        when(mapper.mapToPlanePlaceToResponse(entity)).thenReturn(mapped);

        PlanePlaceResponse result = service.getPlanePlacesByType(type);

        assertNotNull(result);
        assertEquals(11L, result.getId());
        assertEquals(PlaceType.BUSINESS, result.getPlaceType());

        verify(repository, times(1)).findByPlaceType(PlaceType.BUSINESS);
        verify(mapper, times(1)).mapToPlanePlaceToResponse(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getPlanePlacesByType_whenNotFound_throwsRuntimeException() {
        String type = "BUSINESS";
        when(repository.findByPlaceType(PlaceType.BUSINESS)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getPlanePlacesByType(type));

        verify(repository, times(1)).findByPlaceType(PlaceType.BUSINESS);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
    @Test
    void getPlanePlacesByType_whenInvalidEnum_throwsIllegalArgument_andSkipsRepo() {
        String invalid = "window";

        assertThrows(IllegalArgumentException.class, () -> service.getPlanePlacesByType(invalid));

        verifyNoInteractions(repository, mapper);
    }

    @Test
    void getPlanePlacesByRow() {
        Integer row = 3;
        PlanePlace entity = new PlanePlace();
        entity.setId(31L);

        PlanePlaceResponse mapped = PlanePlaceResponse.builder()
                .id(31L).place(1).row(row).placeNumber(14).placeType(PlaceType.BUSINESS).build();

        when(repository.findByRow(row)).thenReturn(Optional.of(entity));
        when(mapper.mapToPlanePlaceToResponse(entity)).thenReturn(mapped);

        PlanePlaceResponse result = service.getPlanePlacesByRow(row);

        assertNotNull(result);
        assertEquals(31L, result.getId());
        assertEquals(row, result.getRow());
        assertEquals(PlaceType.BUSINESS, result.getPlaceType());

        verify(repository, times(1)).findByRow(row);
        verify(mapper, times(1)).mapToPlanePlaceToResponse(entity);
        verifyNoMoreInteractions(repository, mapper);
    }
    @Test
    void getPlanePlacesByRow_whenNotFound_throwsRuntimeException() {
        Integer row = 99;
        when(repository.findByRow(row)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getPlanePlacesByRow(row));

        verify(repository, times(1)).findByRow(row);
        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(repository);
    }
}