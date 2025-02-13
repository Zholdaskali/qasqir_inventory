package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
    private String name;
    private Long updateBy;
}
