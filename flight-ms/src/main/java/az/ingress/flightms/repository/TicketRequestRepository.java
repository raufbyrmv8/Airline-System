package az.ingress.flightms.repository;
import az.ingress.flightms.model.entity.TicketRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TicketRequestRepository extends JpaRepository<TicketRequest, Long> {
    Optional<TicketRequest> findByIdAndStatusAndExpiredDateGreaterThan(Long id, Boolean status, LocalDateTime now);
}