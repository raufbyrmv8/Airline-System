package az.ingress.userms.model.dto.response;

public record UserResponseDto(
        Long id,
        String name,
        String surname,
        String email) {
}
