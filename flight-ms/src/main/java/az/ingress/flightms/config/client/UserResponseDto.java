package az.ingress.flightms.config.client;

public record UserResponseDto(
        Long id,
        String name,
        String surname,
        String email) {
}
