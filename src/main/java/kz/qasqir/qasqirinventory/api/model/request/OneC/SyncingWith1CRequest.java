package kz.qasqir.qasqirinventory.api.model.request.OneC;

import lombok.Data;

import java.util.List;

@Data
public class SyncingWith1CRequest {
    List<SyncingWith1CItemRequest> items;
}
