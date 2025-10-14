package az.ingress.flightms.service.impl;
import az.ingress.flightms.exception.NotFoundException;
import az.ingress.flightms.mapper.FlightPlanePlaceMapper;
import az.ingress.flightms.model.dto.request.FlightPlanePlaceDto;
import az.ingress.flightms.repository.FlightPlanePlaceRepository;
import az.ingress.flightms.service.FlightPlanePlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static az.ingress.flightms.model.enums.Exceptions.NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
@Service
public class FlightPlanePlaceServiceImpl implements FlightPlanePlaceService {

    private final FlightPlanePlaceRepository flightPlanePlaceRepository;
    private final FlightPlanePlaceMapper flightPlanePlaceMapper;

    @Override
    public FlightPlanePlaceDto update(Long id, FlightPlanePlaceDto dto) {
        var entity = flightPlanePlaceRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));
        flightPlanePlaceMapper.updateEntity(entity, dto);
        return null;
    }

    @Override
    public FlightPlanePlaceDto getById(Long id) {
        return flightPlanePlaceRepository.findById(id)
                .map(flightPlanePlaceMapper::toDto)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
    }

    @Override
    public List<FlightPlanePlaceDto> getAll() {
        return flightPlanePlaceRepository.findAll().stream()
                .map(flightPlanePlaceMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        var existedEntity = flightPlanePlaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND));
        existedEntity.setStatus(false);
        flightPlanePlaceRepository.save(existedEntity);
    }

}
