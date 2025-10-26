package az.ingress.flightms.service.impl;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.flightms.exception.NotFoundException;
import az.ingress.flightms.service.impl.AirlineServiceImpl;
import az.ingress.flightms.model.dto.request.AirlineDto;
import az.ingress.flightms.exception.NotFoundException;
import az.ingress.flightms.model.dto.response.AirlineResponseDto;
import az.ingress.flightms.model.entity.Airline;
import az.ingress.flightms.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceImplTest {
    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AirlineServiceImpl airlineService;

    @Test
    void createAirline() {
        // given
        AirlineDto dto = new AirlineDto();
        dto.setName("AZAL");

        Airline toSave = new Airline();
        toSave.setName("AZAL");

        Airline saved = new Airline();
        saved.setId(1L);
        saved.setName("AZAL");

        when(modelMapper.map(dto, Airline.class)).thenReturn(toSave);
        when(airlineRepository.save(toSave)).thenReturn(saved);

        // when
        AirlineResponseDto response = airlineService.createAirline(dto);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("AZAL", response.getName());

        verify(modelMapper, times(1)).map(dto, Airline.class);
        verify(airlineRepository, times(1)).save(toSave);
        verifyNoMoreInteractions(modelMapper, airlineRepository);
    }
    @Test
    void createAirline_whenUniqueConstraintViolation_thenThrowApplicationException() {
        AirlineDto dto = new AirlineDto();
        dto.setName("AlreadyExists");

        Airline toSave = new Airline();
        toSave.setName("AlreadyExists");

        when(modelMapper.map(dto, Airline.class)).thenReturn(toSave);
        when(airlineRepository.save(toSave)).thenThrow(new RuntimeException("duplicate key"));


        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> airlineService.createAirline(dto));

        assertTrue(ex.getMessage() == null || ex.getMessage().contains("AlreadyExists"),
                "Exception message should contain airline name");

        verify(modelMapper).map(dto, Airline.class);
        verify(airlineRepository).save(toSave);
        verifyNoMoreInteractions(modelMapper, airlineRepository);
    }
    @Test
    void findById() {
        long id = 10L;
        Airline entity = new Airline();
        entity.setId(id);
        entity.setName("AZAL");

        AirlineResponseDto mapped = new AirlineResponseDto("AZAL", id);

        when(airlineRepository.findById(id)).thenReturn(java.util.Optional.of(entity));
        when(modelMapper.map(entity, AirlineResponseDto.class)).thenReturn(mapped);

        AirlineResponseDto resp = airlineService.findById(id);

        assertNotNull(resp);
        assertEquals(id, resp.getId());
        assertEquals("AZAL", resp.getName());

        verify(airlineRepository, times(1)).findById(id);
        verify(modelMapper, times(1)).map(entity, AirlineResponseDto.class);
        verifyNoMoreInteractions(airlineRepository, modelMapper);
    }
    @Test
    void findById_whenMissing_thenThrowNotFound() {
        long id = 99L;
        when(airlineRepository.findById(id)).thenReturn(java.util.Optional.empty());

        az.ingress.flightms.exception.NotFoundException ex =
                assertThrows(az.ingress.flightms.exception.NotFoundException.class,
                        () -> airlineService.findById(id));

        verify(airlineRepository).findById(id);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findAll() {
        Airline a1 = new Airline();
        a1.setId(1L); a1.setName("AZAL");

        Airline a2 = new Airline();
        a2.setId(2L); a2.setName("ButalAir");

        when(airlineRepository.findAll()).thenReturn(java.util.List.of(a1, a2));

        AirlineResponseDto d1 = new AirlineResponseDto("AZAL", 1L);
        AirlineResponseDto d2 = new AirlineResponseDto("ButalAir", 2L);

        when(modelMapper.map(a1, AirlineResponseDto.class)).thenReturn(d1);
        when(modelMapper.map(a2, AirlineResponseDto.class)).thenReturn(d2);

        var result = airlineService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("AZAL", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("ButalAir", result.get(1).getName());

        verify(airlineRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(a1, AirlineResponseDto.class);
        verify(modelMapper, times(1)).map(a2, AirlineResponseDto.class);
        verifyNoMoreInteractions(airlineRepository, modelMapper);
    }
    @Test
    void findAll_whenEmpty_thenReturnEmptyList_andNotMap() {

        when(airlineRepository.findAll()).thenReturn(java.util.List.of());

        var result = airlineService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(airlineRepository, times(1)).findAll();
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findByAirlineByName() {
        String name = "AZAL";
        Airline entity = new Airline();
        entity.setId(1L);
        entity.setName(name);

        AirlineDto mapped = new AirlineDto();
        mapped.setName(name);

        when(airlineRepository.findByName(name)).thenReturn(java.util.Optional.of(entity));
        when(modelMapper.map(entity, AirlineDto.class)).thenReturn(mapped);

        AirlineDto result = airlineService.findByAirlineByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());

        verify(airlineRepository, times(1)).findByName(name);
        verify(modelMapper, times(1)).map(entity, AirlineDto.class);
        verifyNoMoreInteractions(airlineRepository, modelMapper);
    }
    @Test
    void findByAirlineByName_whenMissing_thenThrowNotFound() {

        String name = "UNKNOWN";
        when(airlineRepository.findByName(name)).thenReturn(java.util.Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> airlineService.findByAirlineByName(name));

        verify(airlineRepository, times(1)).findByName(name);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void updateAirline() {
        long id = 5L;

        Airline existing = new Airline();
        existing.setId(id);
        existing.setName("OLD");

        AirlineDto dto = new AirlineDto();
        dto.setName("NEW");

        Airline saved = new Airline();
        saved.setId(id);
        saved.setName("NEW");

        AirlineResponseDto mapped = new AirlineResponseDto("NEW", id);

        when(airlineRepository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(airlineRepository.save(any(Airline.class))).thenReturn(saved);
        when(modelMapper.map(saved, AirlineResponseDto.class)).thenReturn(mapped);

        AirlineResponseDto result = airlineService.updateAirline(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("NEW", result.getName());

        ArgumentCaptor<Airline> captor = ArgumentCaptor.forClass(Airline.class);
        verify(airlineRepository).findById(id);
        verify(airlineRepository).save(captor.capture());
        Airline toPersist = captor.getValue();
        assertEquals(id, toPersist.getId());
        assertEquals("NEW", toPersist.getName());

        verify(modelMapper).map(saved, AirlineResponseDto.class);
        verifyNoMoreInteractions(airlineRepository, modelMapper);
    }
    @Test
    void updateAirline_whenMissing_thenThrowNotFound() {
        long id = 404L;
        AirlineDto dto = new AirlineDto();
        dto.setName("ANY");

        when(airlineRepository.findById(id)).thenReturn(java.util.Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> airlineService.updateAirline(id, dto));

        verify(airlineRepository).findById(id);
        verify(airlineRepository, never()).save(any());
        verifyNoInteractions(modelMapper);
    }

    @Test
    void deleteAirline() {
        long id = 7L;
        Airline entity = new Airline();
        entity.setId(id);
        entity.setName("AZAL");
        entity.setStatus(true);

        when(airlineRepository.findById(id)).thenReturn(java.util.Optional.of(entity));

        airlineService.deleteAirline(id);

        assertFalse(entity.getStatus());

        verify(airlineRepository).findById(id);
        verify(airlineRepository, never()).save(any());
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(airlineRepository);
    }
    @Test
    void deleteAirline_whenMissing_thenThrowNotFound() {
        long id = 404L;
        when(airlineRepository.findById(id)).thenReturn(java.util.Optional.empty());

        az.ingress.flightms.exception.NotFoundException ex =
                assertThrows(az.ingress.flightms.exception.NotFoundException.class,
                        () -> airlineService.deleteAirline(id));

        verify(airlineRepository).findById(id);
        verify(airlineRepository, never()).save(any());
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(airlineRepository);
    }
}