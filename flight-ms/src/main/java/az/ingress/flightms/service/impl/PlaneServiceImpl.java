package az.ingress.flightms.service.impl;


import az.ingress.flightms.exception.NotFoundException;
import az.ingress.flightms.mapper.PlaneMapper;
import az.ingress.flightms.model.dto.request.PlaneRequestDTO;
import az.ingress.flightms.model.dto.response.PlaneResponseDTO;
import az.ingress.flightms.model.entity.Airline;
import az.ingress.flightms.model.entity.Plane;
import az.ingress.flightms.model.entity.PlanePlace;
import az.ingress.flightms.repository.AirlineRepository;
import az.ingress.flightms.repository.PlanePlaceRepository;
import az.ingress.flightms.repository.PlaneRepository;
import az.ingress.flightms.service.PlaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static az.ingress.flightms.model.enums.Exceptions.NOT_FOUND;


@Service
@RequiredArgsConstructor
public class PlaneServiceImpl implements PlaneService {

    private final PlaneRepository planeRepository;
    private final AirlineRepository airlineRepository;
    private final PlanePlaceRepository planePlaceRepository;
    private final PlaneMapper planeMapper;

    @Override
    public Long createPlane(PlaneRequestDTO dto) {

        Airline airline = airlineRepository.findById(dto.getAirlineId()).orElseThrow(() -> new NotFoundException(NOT_FOUND, "Plane not found with ID: " + dto.getAirlineId()));
        Set<PlanePlace> planePlace = planePlaceRepository.findAllById(dto.getPlanePlacesIds()).stream().collect(Collectors.toSet());

        if (planePlace.size() != dto.getPlanePlacesIds().size()) {

            Set<Long> missingIds = new HashSet<>(dto.getPlanePlacesIds());
            missingIds.removeAll(planePlace.stream()
                    .map(PlanePlace::getId)
                    .collect(Collectors.toSet()));

            throw new NotFoundException(NOT_FOUND,
                    "Mismatch in PlanePlace IDs. Total requested IDs: " + dto.getPlanePlacesIds().size() +
                            ", Found IDs: " + planePlace.size() +
                            ". Missing IDs: " + missingIds);
        }

        Plane plane = Plane.builder()
                .name(dto.getName())
                .capacity(planePlace.size())
                .airline(airline)
                .planePlaces(planePlace)
                .createdDate(LocalDateTime.now())
                .status(true)
                .build();

        planeRepository.save(plane);

        return plane.getId();
    }

    @Override
    public PlaneResponseDTO updatePlane(Long id, PlaneRequestDTO dto) {

        Plane plane = planeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND, "Plane not found with ID: " + id));
        Airline airline = airlineRepository.findById(dto.getAirlineId()).orElseThrow(() -> new NotFoundException(NOT_FOUND, "Airline not found with ID: " + dto.getAirlineId()));
        Set<PlanePlace> planePlace = planePlaceRepository.findAllById(dto.getPlanePlacesIds()).stream().collect(Collectors.toSet());

        if (planePlace.size() != dto.getPlanePlacesIds().size()) {

            Set<Long> missingIds = new HashSet<>(dto.getPlanePlacesIds());
            missingIds.removeAll(planePlace.stream()
                    .map(PlanePlace::getId)
                    .collect(Collectors.toSet()));

            throw new NotFoundException(NOT_FOUND,
                    "Mismatch in PlanePlace IDs. Total requested IDs: " + dto.getPlanePlacesIds().size() +
                            ", Found IDs: " + planePlace.size() +
                            ". Missing IDs: " + missingIds);
        }

        plane.setName(dto.getName());
        plane.setCapacity(planePlace.size());
        plane.setAirline(airline);
        plane.setPlanePlaces(planePlace);
        plane.setUpdatedDate(LocalDateTime.now());

        planeRepository.save(plane);

        return planeMapper.planeToPlaneResponseDTO(plane);
    }

    @Override
    public void deletePlane(Long id) {

        Optional<Plane> plane = planeRepository.findById(id);
        plane.ifPresent(value -> value.setStatus(false));

    }

    @Override
    public PlaneResponseDTO getPlane(Long id) {

        Plane plane = planeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND, "Plane not found with ID: " + id));
        return planeMapper.planeToPlaneResponseDTO(plane);
    }

    @Override
    public Set<PlaneResponseDTO> getAllPlanes() {

        return planeRepository.findAll().stream()
                .map(planeMapper::planeToPlaneResponseDTO)
                .collect(Collectors.toSet());
    }

}
