package kz.qasqir.qasqirinventory.api.model.request.OneC;

import lombok.Data;

import java.util.List;

@Data
public class OneCWriteOffRequest {
    private List<OneCWriteOffItemRequest> items;
    private String comment;
    private Long createBy;
    private String documentNumber;
    private String type;
}
