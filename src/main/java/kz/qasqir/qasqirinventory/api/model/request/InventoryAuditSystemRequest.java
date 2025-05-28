package kz.qasqir.qasqirinventory.api.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryAuditSystemRequest {
    @NotNull(message = "ID пользователя обязателен")
    private Long createdById;
}
