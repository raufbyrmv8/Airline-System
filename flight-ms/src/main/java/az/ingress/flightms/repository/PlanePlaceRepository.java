package az.ingress.flightms.repository;


import az.ingress.flightms.model.entity.PlanePlace;
import az.ingress.flightms.model.enums.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlanePlaceRepository extends JpaRepository<PlanePlace, Long> {

    Optional<PlanePlace> findByPlaceType(PlaceType placeType);

    Optional<PlanePlace> findByRow(Integer row);

    Optional<PlanePlace> findByIdAndStatus(Long id, Boolean status);

    @Query(value = """
             select p.id as id,p.place_number as placeNumber
             from plane_place p
             right join plane_plane_places ppp on p.id = ppp.plane_places_id
             where p.status = 'true' and ppp.plane_id = ?1 and (p.id not in (?2) or ?2 is null)
            """,nativeQuery = true)
    List<Map<String,Object>> findPlanePlaceByFlightId(Long flightId,List<Integer> capturedSeats);
}
