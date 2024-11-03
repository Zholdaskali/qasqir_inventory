package kz.qasqir.qasqirinventory.api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;


public class MessageResponse<T> {
   private T body = null;
   private String message = null;


   public MessageResponse(T body, String message) {
        this.body = body;
        this.message = message;
   }

    public static <T> MessageResponse<T> of(T body) {
        return new MessageResponse<>(body, null);
    }

    public static <T> MessageResponse<T> empty(String message) {
        return new MessageResponse<>(null, message);
    }

    public T getBody() {
        return body;
    }

    public String getMessage() {
        return message;
    }
}
