package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CustomerRequest {
    private String name;
    private String contactInfo;
}
