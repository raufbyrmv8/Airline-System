package az.ingress.common.kafka;

public record OperatorNotification(Long flightId, String approvalState, String comments) {}