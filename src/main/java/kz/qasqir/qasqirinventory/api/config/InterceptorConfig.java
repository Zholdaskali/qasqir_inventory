package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.Interceptor.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class  InterceptorConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final ActionLogInterceptor actionLogInterceptor;
    private final InviteInterceptor inviteInterceptor;
    private final UserIdInterceptor userIdInterceptor;
    private final PasswordResetTokenInterceptor passwordResetTokenInterceptor;

    public InterceptorConfig(AuthInterceptor authInterceptor, ActionLogInterceptor actionLogInterceptor, InviteInterceptor inviteInterceptor, UserIdInterceptor userIdInterceptor, PasswordResetTokenInterceptor passwordResetTokenInterceptor) {
        this.authInterceptor = authInterceptor;
        this.actionLogInterceptor = actionLogInterceptor;
        this.inviteInterceptor = inviteInterceptor;
        this.userIdInterceptor = userIdInterceptor;
        this.passwordResetTokenInterceptor = passwordResetTokenInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(1)
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/auth/sign-in", "/api/v1/version", "/api/v1/user/password/reset")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**", "/api/v1/user/password/recovery");
        registry.addInterceptor(userIdInterceptor)
                .order(2)
                .addPathPatterns("/api/v1/user/**")
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/user/password/recovery", "/api/v1/user/password/reset");
        registry.addInterceptor(actionLogInterceptor)
                .order(3)
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/auth/sign-in", "/api/v1/version", "/api/v1/user/password/recovery")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**", "/api/v1/admin/log/**", "/api/v1/user/password/reset");
        registry.addInterceptor(inviteInterceptor)
                .order(4)
                .addPathPatterns("/api/v1/user/password/reset-invite");
        registry.addInterceptor(passwordResetTokenInterceptor)
                .order(5)
                .addPathPatterns("/api/v1/user/password/reset");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // или "*" для разрешения всех
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders("Auth-token", "auth-token")
                .allowCredentials(true);
    }

}

