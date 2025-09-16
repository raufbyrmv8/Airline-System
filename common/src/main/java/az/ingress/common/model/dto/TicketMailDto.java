package az.ingress.common.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketMailDto {
    String ticketContent;
    String email;
    String subject;
}
