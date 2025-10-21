package az.ingress.notificationms.service.listener;


import az.ingress.common.kafka.OperatorRegisterDto;

public interface OperatorRegisterListenerService {
    void sendNotificationEmail(OperatorRegisterDto operatorRegisterDto);

    void sendNotificationApp(OperatorRegisterDto operatorRegisterDto);
}
