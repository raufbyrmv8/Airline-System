package az.ingress.userms.repository;
import az.ingress.userms.model.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    @Query(value = "SELECT v.* FROM verification v WHERE v.token =?1 AND v.is_expired = false AND v.is_used = false and v.expiration_time  > CURRENT_TIMESTAMP and v.type =?2 ",nativeQuery = true)
    Optional<Verification> getValidToken(String token, String type);
}