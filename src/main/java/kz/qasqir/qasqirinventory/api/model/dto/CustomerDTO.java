package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String contactInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
