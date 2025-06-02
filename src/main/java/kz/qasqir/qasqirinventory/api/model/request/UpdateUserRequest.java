package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String userName;
    private String userNumber;
}
