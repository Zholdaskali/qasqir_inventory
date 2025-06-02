package kz.qasqir.qasqirinventory.api.model.request;

import lombok.Data;

@Data
public class UpdateEmailGenerateRequest {
    private Long userId;
    private String email;
    private String password;
}
