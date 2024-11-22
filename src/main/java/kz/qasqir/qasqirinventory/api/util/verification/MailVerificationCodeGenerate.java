package kz.qasqir.qasqirinventory.api.util.verification;

import java.util.Random;

public class MailVerificationCodeGenerate {
    private final int sizeCode = 6;
    private final Random random = new Random();
    private final char[] characters = "0123456789".toCharArray();
    public String generate() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < sizeCode; i++) {
            code.append(characters[random.nextInt(characters.length)]);
        }
        return code.toString();
    }
}
