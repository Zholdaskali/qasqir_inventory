package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;


@Data
public class WareHouseSaveRequest {
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
}
