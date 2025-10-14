package az.ingress.flightms.service;


import az.ingress.flightms.model.dto.request.PlaneRequestDTO;
import az.ingress.flightms.model.dto.response.PlaneResponseDTO;

import java.util.Set;

public interface PlaneService {

    Long createPlane(PlaneRequestDTO planeRequestDTO);
    PlaneResponseDTO updatePlane(Long id, PlaneRequestDTO planeRequestDTO);
    void deletePlane(Long id);
    PlaneResponseDTO getPlane(Long id);
    Set<PlaneResponseDTO> getAllPlanes();

}
