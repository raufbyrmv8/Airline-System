package az.ingress.flightms.model.entity;

import az.ingress.common.model.entity.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest extends AbstractEntity {
    private Long createdUserId;
    private Long flightId;
    private Long planePlaceId;
    private LocalDateTime expiredDate;
}
