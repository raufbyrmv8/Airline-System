package az.ingress.flightms.model.entity;
import az.ingress.common.model.entity.AbstractEntity;
import az.ingress.flightms.model.enums.Airport;
import az.ingress.flightms.model.enums.ApprovalState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("status = true")
@DynamicUpdate
public class Flight extends AbstractEntity {

    @Column(name = "\"from\"")
    @Enumerated(EnumType.STRING)
    private Airport from;

    @Column(name = "\"to\"")
    @Enumerated(EnumType.STRING)
    private Airport to;

    private BigDecimal price;
    private Integer ticketCount;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    @Column(name = "feedback_message", length = 1000)
    @Nationalized
    private String feedbackMessage;
    @ManyToOne
    private Plane plane;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_state")
    private ApprovalState approvalState;
}
