package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.Interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final ActionLogInterceptor actionLogInterceptor;
    private final InviteInterceptor inviteInterceptor;
    private final PasswordResetTokenInterceptor passwordResetTokenInterceptor;
    private final Bitrix24Interceptor bitrix24Interceptor;

    private static final String[] COMMON_EXCLUDE_PATTERNS = {
            "/api/v1/user/password/reset-invite",
            "/api/v1/auth/sign-in",
            "/api/v1/version",
            "/api/v1/user/password/reset",
            "/swagger-ui.html",
            "/api-docs/**",
            "/swagger-ui/**",
            "/api/v1/user/password/recovery"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(1)
                .excludePathPatterns(COMMON_EXCLUDE_PATTERNS)
                .excludePathPatterns("/api/v1/bitrix24/**");

        registry.addInterceptor(actionLogInterceptor)
                .order(3)
                .addPathPatterns(
                        "/api/v1/warehouse-manager/**",
                        "/api/v1/admin/**",
                        "/api/v1/user/profile/**",
                        "/api/v1/storekeeper/**",
                        "/api/v1/1C/**"
                )
                .excludePathPatterns(COMMON_EXCLUDE_PATTERNS)
                .excludePathPatterns("**/log/**");

        registry.addInterceptor(inviteInterceptor)
                .order(4)
                .addPathPatterns("/api/v1/user/password/reset-invite");

        registry.addInterceptor(passwordResetTokenInterceptor)
                .order(5)
                .addPathPatterns("/api/v1/user/password/reset");

        registry.addInterceptor(bitrix24Interceptor)
                .order(6)
                .addPathPatterns("/api/v1/bitrix24/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5173", "http://192.168.1.*:5173", "http://172.16.80.*:5173") // Разрешить все IP в подсети 192.168.1.*
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders("Auth-token", "auth-token")
                .allowCredentials(true);
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOriginPatterns("https://qasqir-inventory-web-client.onrender.com") // Разрешить все IP в подсети 192.168.1.*
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .exposedHeaders("Auth-token", "auth-token")
//                .allowCredentials(true);
//    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:5173", "http://192.168.1.5:5173") // или "*" для разрешения всех
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .exposedHeaders("Auth-token", "auth-token")
//                .allowCredentials(true);
//    }
}