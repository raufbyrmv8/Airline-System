package az.ingress.notificationms.repository;


import az.ingress.notificationms.model.entity.Notification;
import az.ingress.notificationms.model.enums.NotificationState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<List<Notification>> findAllByIdIn(List<Long> ids);
    Optional<List<Notification>> findAllByState(NotificationState status);
}
