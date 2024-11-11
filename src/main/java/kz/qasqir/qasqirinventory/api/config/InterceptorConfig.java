package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.Interceptor.Auth.ActionLogInterceptor;
import kz.qasqir.qasqirinventory.api.Interceptor.Auth.AuthInterceptor;
import kz.qasqir.qasqirinventory.api.Interceptor.Auth.InviteInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class  InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private ActionLogInterceptor actionLogInterceptor;
    @Autowired
    private InviteInterceptor inviteInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(1)
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/auth/sign-in", "/api/v1/version")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**");

        registry.addInterceptor(actionLogInterceptor)
                .order(2)
                .excludePathPatterns("/api/v1/user/password/reset-invite", "/api/v1/auth/sign-in", "/api/v1/version")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**");
        registry.addInterceptor(inviteInterceptor)
                .order(3)
                .excludePathPatterns("/api/v1/auth/sign-in", "/api/v1/version")
                .excludePathPatterns("/swagger-ui.html", "/api-docs/**", "/swagger-ui/**");
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

