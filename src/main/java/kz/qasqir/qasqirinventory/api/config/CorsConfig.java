//package kz.qasqir.qasqirinventory.api.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // Разрешает все пути
//                        .allowedOrigins("*") // Разрешает все источники
//                        .allowedMethods("*") // Разрешает все методы: GET, POST, PUT, DELETE, OPTIONS и другие
//                        .allowedHeaders("*") // Разрешает все заголовки
//                        .allowCredentials(false); // Если куки или авторизация не требуются
//            }
//        };
//    }
//}
