package az.ingress.flightms.specification;
import az.ingress.flightms.model.entity.Flight;
import az.ingress.flightms.model.enums.Airport;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Scope("prototype")
public class FlightSpecification {
    public Specification<Flight> search(String to, String from, LocalDateTime initialDate, BigDecimal initialPrice) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (from != null) {
                List<Airport> airportsFrom = Airport.findByCity(from);
                if (airportsFrom.isEmpty()) {
                    airportsFrom = Airport.findByCountry(from);
                }
                if (!airportsFrom.isEmpty()) {
                    CriteriaBuilder.In<String> fromAirportCodes = criteriaBuilder.in(root.get("from"));
                    airportsFrom.forEach(airport -> fromAirportCodes.value(airport.name()));
                    predicate = criteriaBuilder.and(predicate, fromAirportCodes);
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("from"), from));
                }
            }
            if (to != null) {
                List<Airport> airportsTo = Airport.findByCity(to);
                if (airportsTo.isEmpty()) {
                    airportsTo = Airport.findByCountry(to);
                }
                if (!airportsTo.isEmpty()) {
                    CriteriaBuilder.In<String> toAirportCodes = criteriaBuilder.in(root.get("to"));
                    airportsTo.forEach(airport -> toAirportCodes.value(airport.name()));
                    predicate = criteriaBuilder.and(predicate, toAirportCodes);
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("to"), to));
                }
            }
            if (initialDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("departureTime"), initialDate));
            }
            if (initialPrice != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("price"), initialPrice));
            }
            return predicate;
        };
    }
}
