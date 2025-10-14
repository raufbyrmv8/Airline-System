package az.ingress.flightms.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingSearchResponseDto {
    private Long flightId;
    private String from;
    private String to;
    private LocalDateTime date;
    private BigDecimal price;
}
