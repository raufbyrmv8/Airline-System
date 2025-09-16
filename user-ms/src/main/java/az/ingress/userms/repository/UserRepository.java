package az.ingress.userms.repository;

import az.ingress.common.model.constant.Roles;
import az.ingress.userms.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByStatusAndEmailAndIsActiveAndIsEnabled(Boolean status, String email, Boolean isActive, Boolean isEnabled);

    @Query("select u.email from _user as u where u.role = :role")
    Optional<List<String>> findEmailsByRole(@Param("role") Roles role);
}