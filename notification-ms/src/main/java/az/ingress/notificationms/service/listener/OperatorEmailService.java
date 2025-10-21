package az.ingress.notificationms.service.listener;


import az.ingress.common.kafka.OperatorNotificationDto;

public interface OperatorEmailService {
    void sendNotification(OperatorNotificationDto operatorNotificationDto);
}
