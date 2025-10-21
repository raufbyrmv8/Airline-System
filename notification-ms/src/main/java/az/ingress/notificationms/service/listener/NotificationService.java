package az.ingress.notificationms.service.listener;


import az.ingress.notificationms.model.dto.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    List<NotificationResponseDto> getNotificationsByState(String state);

    void markAsRead(List<Long> ids);

}
