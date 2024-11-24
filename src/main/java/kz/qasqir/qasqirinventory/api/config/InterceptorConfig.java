package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.Interceptor.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class  InterceptorConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final ActionLogInterceptor actionLogInterceptor;
    private final InviteInterceptor inviteInterceptor;
    private final UserIdInterceptor userIdInterceptor;

    public InterceptorConfig(AuthInterceptor authInterceptor, ActionLogInterceptor actionLogInterceptor, InviteInterceptor inviteInterceptor, UserIdInterceptor userIdInterceptor) {
        this.authInterceptor = authInterceptor;
        this.actionLogInterceptor = actionLogInterceptor;
        this.inviteInterceptor = inviteInterceptor;
        this.userIdInterceptor = userIdInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(1)
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/auth/sign-in", "/api/v1/version")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**");
        registry.addInterceptor(userIdInterceptor)
                .order(2)
                .addPathPatterns("/api/v1/user/**");
        registry.addInterceptor(actionLogInterceptor)
                .order(3)
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/auth/sign-in", "/api/v1/version")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**", "/api/v1/super-admin/log/**");
        registry.addInterceptor(inviteInterceptor)
                .order(4)
                .addPathPatterns("/api/v1/user/password/reset-invite");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // или "*" для разрешения всех
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders("Auth-token", "auth-token")
                .allowCredentials(true);
    }

}

