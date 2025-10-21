package az.ingress.common.kafka;

public record UserResetPasswordDto(String email, String firstName, String lastName, String token) {
}
