package az.ingress.flightms.repository;
import az.ingress.flightms.model.entity.Flight;
import az.ingress.flightms.model.enums.ApprovalState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
    boolean existsByIdAndStatusAndTicketCountGreaterThanAndApprovalState(Long aLong, boolean b, Integer ticketCount, ApprovalState state);

    @Query(value = """
            select case when count(*) > 0 then true else false end
            from flight f
            inner join plane p on f.plane_id = p.id
            inner join plane_plane_places ppp on p.id = ppp.plane_id and ppp.plane_places_id =?2
            where f.status = 'true' and p.status = 'true' and f.id =?1
            """, nativeQuery = true)
    boolean existPlanePlaceByPlanePlaceId(Long id, Long planePlaceId);

    List<Flight> findByApprovalState(ApprovalState state);

    Optional<Flight> findByIdAndStatus(Long id, Boolean status);
    
}
