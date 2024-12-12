package kz.qasqir.qasqirinventory.api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class WareHouseSaveRequest {
    private String name;
    private String location;
}
