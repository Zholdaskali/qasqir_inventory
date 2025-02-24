package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WarehouseContainerDTO {
    private Long id;
    private String serialNumber;
    private BigDecimal capacity;
    private Double width;
    private Double height;
    private Double length;
}
