package az.ingress.notificationms.service.listener.impl;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.notificationms.mapper.NotificationMapper;
import az.ingress.notificationms.model.dto.NotificationResponseDto;
import az.ingress.notificationms.model.entity.Notification;
import az.ingress.notificationms.model.enums.NotificationState;
import az.ingress.notificationms.repository.NotificationRepository;
import az.ingress.notificationms.service.listener.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static az.ingress.notificationms.model.enums.Exceptions.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationResponseDto> getNotificationsByState(String state) {

        List<Notification> notifications = notificationRepository.findAllByState(NotificationState.valueOf(state.toUpperCase())).orElseThrow(() -> new ApplicationException(NOT_FOUND));

        notifications.forEach(notification -> notification.setSentAt(LocalDateTime.now()));

        List<NotificationResponseDto> list = notifications.stream()
                .map(notificationMapper::notificationToResponseDto)
                .toList();

        notificationRepository.saveAll(notifications);

        return list;
    }

    @Override
    public void markAsRead(List<Long> ids) {

        List<Notification> notifications = notificationRepository.findAllByIdIn(ids).orElseThrow(() -> new ApplicationException(NOT_FOUND));

        notifications.forEach(notification -> {
            notification.setState(NotificationState.READ);
        });
        notificationRepository.saveAll(notifications);
    }
}
