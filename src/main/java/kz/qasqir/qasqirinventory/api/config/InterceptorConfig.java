package kz.qasqir.qasqirinventory.api.config;

import kz.qasqir.qasqirinventory.api.Interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .order(0)
                .excludePathPatterns("/api/v1/auth/sign-up")
                .excludePathPatterns("/api/v1/auth/invite-sign-up")
                .excludePathPatterns("/api/v1/auth/sign-in")
                .excludePathPatterns("/api/v1/version")
                .excludePathPatterns("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs");
    }
}
