package beyond.ordersystem.common.configs;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

//    dd
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("https://www.yeskiticket.shop")    // 허용 url 명시
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
