package az.ingress.notificationms.service.listener;


import az.ingress.common.kafka.AdminNotificationDto;

public interface AdminEmailService {
    void sendNotification(AdminNotificationDto adminNotificationDto);
}
