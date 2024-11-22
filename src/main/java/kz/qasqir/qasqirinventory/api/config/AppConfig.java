package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.util.encoder.BCryptPasswordEncoder;
import kz.qasqir.qasqirinventory.api.util.token.RandomStringTokenGenerator;
import kz.qasqir.qasqirinventory.api.util.token.TokenGenerator;
import kz.qasqir.qasqirinventory.api.util.verification.MailVerificationCodeGenerate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

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
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("tmqasqirinventory@gmail.com");
        mailSender.setPassword("lhvmohlhvtovxykt");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
