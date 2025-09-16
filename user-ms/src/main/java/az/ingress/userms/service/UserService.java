package az.ingress.userms.service;
import az.ingress.userms.model.dto.response.UserResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    UserResponseDto getUserDetailsById(Long id);
    UserResponseDto getUserDetailsByEmail(String email);
}
