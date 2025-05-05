package kz.qasqir.qasqirinventory.api.model.request.OneC;

import lombok.Data;

@Data
public class SyncingWith1CItemRequest {
    private String name;
    private String article;
    private String code;
    private String measurementUnit;
    private String categoryName;
    private String type;
    private String tnvedCode;
}
