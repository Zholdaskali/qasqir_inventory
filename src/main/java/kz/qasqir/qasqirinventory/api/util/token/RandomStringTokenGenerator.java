package kz.qasqir.qasqirinventory.api.util.token;
import java.util.*;

public class RandomStringTokenGenerator implements TokenGenerator {
    private final int keySize;
    private final Random random = new Random();
    private final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public RandomStringTokenGenerator(int keySize)   {
        this.keySize = keySize;
    }
    @Override
    public String generate() {
        StringBuilder token = new StringBuilder(keySize);
        for(int i = 0; i < keySize; i++) {
            token.append(characters[random.nextInt(characters.length)]);
        }
        return token.toString();
    }
}
