package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryUpdateRequest {
    private Long categoryId;
    private String name;
    private Long updateBy;
}
