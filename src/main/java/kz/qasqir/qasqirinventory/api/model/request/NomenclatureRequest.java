package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NomenclatureRequest {
    private String name;
    private String article;
    private String code;
    private String type;
    private Long updated_by;
    private String tnved_code;
    private String measurement_unit;
}
