package az.ingress.common.kafka;

public record UserRegisterDto(String email, String firstName, String lastName, String token) {
}
