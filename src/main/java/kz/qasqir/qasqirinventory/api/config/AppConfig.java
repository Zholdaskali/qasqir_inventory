package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.util.encoder.BCryptPasswordEncoder;
import kz.qasqir.qasqirinventory.api.util.token.RandomStringTokenGenerator;
import kz.qasqir.qasqirinventory.api.util.token.TokenGenerator;
import kz.qasqir.qasqirinventory.api.util.verification.MailVerificationCodeGenerate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class AppConfig {

    @Bean
    public TokenGenerator apiKeyGenerator() {
        return new RandomStringTokenGenerator(128);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public MailVerificationCodeGenerate mailVerificationCodeGenerate() {
        return new MailVerificationCodeGenerate();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        return new JavaMailSenderImpl();
    }
}
