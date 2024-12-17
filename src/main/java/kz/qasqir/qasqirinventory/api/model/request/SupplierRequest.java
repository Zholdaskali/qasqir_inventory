package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierRequest {
    private String name;
    private String contactInfo;
}
