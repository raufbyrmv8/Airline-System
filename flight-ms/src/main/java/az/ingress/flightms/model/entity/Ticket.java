package az.ingress.flightms.model.entity;
import az.ingress.common.model.entity.AbstractEntity;
import az.ingress.flightms.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Ticket extends AbstractEntity {
    private String ticketNo;
    private String passengerName;
    private String passengerSurname;
    private String email;
    private String phone;
    private Long boughtUserId;
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;
    @ManyToOne
    private Flight flight;
    @OneToOne
    private TicketRequest ticketRequest;
}
