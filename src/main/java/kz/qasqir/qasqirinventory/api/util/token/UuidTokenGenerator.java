package kz.qasqir.qasqirinventory.api.util.token;
import java.util.*;

public class UuidTokenGenerator implements TokenGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
