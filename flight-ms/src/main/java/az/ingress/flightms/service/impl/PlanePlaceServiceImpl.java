package az.ingress.flightms.service.impl;

import az.ingress.flightms.mapper.PlanePlaceMapper;
import az.ingress.flightms.model.dto.request.PlanePlaceRequest;
import az.ingress.flightms.model.dto.response.PlanePlaceResponse;
import az.ingress.flightms.model.entity.PlanePlace;
import az.ingress.flightms.model.enums.PlaceType;
import az.ingress.flightms.repository.PlanePlaceRepository;
import az.ingress.flightms.service.PlanePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanePlaceServiceImpl implements PlanePlaceService {

    private final PlanePlaceRepository planePlaceRepository;
    private final PlanePlaceMapper planePlaceMapper;

    @Override
    public PlanePlaceResponse createPlanePlace(PlanePlaceRequest planePlaceRequest) {

        if (planePlaceRequest.getPlaceNumber() == null || planePlaceRequest.getPlaceType() == null) {
            throw new IllegalArgumentException("Place number and place type are mandatory");
        }

       PlanePlaceResponse response= PlanePlaceResponse.builder()
                .place(planePlaceRequest.getPlace())
                .row(planePlaceRequest.getRow())
                .placeNumber(planePlaceRequest.getPlaceNumber())
                .placeType(planePlaceRequest.getPlaceType())
                .build();

        PlanePlace save = planePlaceRepository.save(planePlaceMapper.mapToPlanePlace(response));

           return planePlaceMapper.mapToPlanePlaceToResponse(save);
    }

    @Override
    public PlanePlaceResponse getPlanePlaceById(Long id) {

        PlanePlace planePlace =planePlaceRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Not found the planePlace with id "));
        return planePlaceMapper.mapToPlanePlaceToResponse(planePlace);


    }

    @Override
    public Set<PlanePlaceResponse> getAllPlanePlaces() {

        return planePlaceRepository.findAll().stream().map(planePlaceMapper::mapToPlanePlaceToResponse)
                .collect(Collectors.toSet());

    }

    @Override
    public PlanePlaceResponse updatePlanePlace(Long id, PlanePlaceRequest planePlaceRequest) {

       PlanePlace planePlace= planePlaceRepository.findById(id)
               .orElseThrow(()->new RuntimeException("Not found the planePlace with id "));

       planePlace.setPlace(planePlaceRequest.getPlace());
       planePlace.setRow(planePlaceRequest.getRow());
       planePlace.setPlaceNumber(planePlaceRequest.getPlaceNumber());
       planePlace.setPlaceType(planePlaceRequest.getPlaceType());

       planePlaceRepository.save(planePlace);

       return planePlaceMapper.mapToPlanePlaceToResponse(planePlace);
    }

    @Override
    public void deletePlanePlace(Long id) {

        Optional<PlanePlace> planePlace = planePlaceRepository.findById(id);
        if (planePlace.isPresent()) {
            planePlaceRepository.delete(planePlace.get());
        }

    }

    @Override
    public PlanePlaceResponse getPlanePlacesByType(String placeType) {
        PlanePlace planePlace= planePlaceRepository.findByPlaceType(PlaceType.valueOf(placeType))
                .orElseThrow(()->new RuntimeException("Not found "));
        return planePlaceMapper.mapToPlanePlaceToResponse(planePlace);

    }

    @Override
    public PlanePlaceResponse getPlanePlacesByRow(Integer row) {
        PlanePlace planePlace= planePlaceRepository.findByRow(row)
                .orElseThrow(()->new RuntimeException("Not found "));
        return planePlaceMapper.mapToPlanePlaceToResponse(planePlace);


    }
}
