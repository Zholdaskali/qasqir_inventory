package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketDTO {
    private Long id;
    private String type;
    private String status;
    private DocumentDTO document;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long managerId;
    private String managerName;
    private LocalDateTime managedAt;
    private String comment;
    private InventoryDTO inventory;
    private BigDecimal quantity;
}



//id           | integer                     |                    | not null          | nextval('t_ticket_id_seq'::regclass)
//type         | character varying(10)       |                    | not null          |
//status       | character varying(10)       |                    | not null          |
//document_id  | bigint                      |                    | not null          |
//create_by    | bigint                      |                    | not null          |
//create_at    | timestamp without time zone |                    | not null          |
//manager_id   | bigint                      |                    | not null          |
//managed_at   | timestamp without time zone |                    | not null          |
//comment      | text                        |                    |                   |
//inventory_id | bigint                      |                    |                   |
//quantity     | numeric(15,3)