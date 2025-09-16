package az.ingress.userms.mapper;

import az.ingress.userms.model.dto.request.UserRequestDto;
import az.ingress.userms.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", constant = "CUSTOMER")
    @Mapping(target = "isEnabled", constant = "false")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "password", ignore = true)
    User map(UserRequestDto userRequestDto);
}
