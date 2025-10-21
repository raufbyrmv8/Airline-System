package az.ingress.notificationms.service.listener;


import az.ingress.common.kafka.UserRegisterDto;

public interface UserRegisterListenerService {
    void sendNotification(UserRegisterDto dto);
}
