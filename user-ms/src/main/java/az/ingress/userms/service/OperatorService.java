package az.ingress.userms.service;

import az.ingress.userms.model.dto.request.OperatorRequestDto;

public interface OperatorService {

    void registerOperator(OperatorRequestDto dto);

    void removeOperatorRole(OperatorRequestDto dto);

    void approvalOperator(OperatorRequestDto dto);

}
