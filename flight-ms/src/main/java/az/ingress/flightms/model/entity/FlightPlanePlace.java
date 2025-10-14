package az.ingress.flightms.model.entity;
import az.ingress.common.model.entity.AbstractEntity;
import az.ingress.flightms.model.enums.PlaceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("status = true")
@SuperBuilder
public class FlightPlanePlace extends AbstractEntity {
    @ManyToOne
    private Flight flight;
    @ManyToOne
    private PlanePlace planePlace;
    @ManyToOne
    private Ticket ticket;
    @Enumerated(EnumType.STRING)
    private PlaceStatus placeStatus;
}
