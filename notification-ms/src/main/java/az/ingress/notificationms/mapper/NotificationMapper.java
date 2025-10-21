package az.ingress.notificationms.mapper;
import az.ingress.common.kafka.OperatorRegisterDto;
import az.ingress.notificationms.model.dto.NotificationResponseDto;
import az.ingress.notificationms.model.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponseDto notificationToResponseDto(Notification notification);
    Notification dtoToEntity(OperatorRegisterDto dto);
}
