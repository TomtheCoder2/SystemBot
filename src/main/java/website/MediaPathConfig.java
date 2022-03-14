package website;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MediaPathConfig {
//    // I assign filePath and pathPatterns using @Value annotation
//    private final String filePath = "/**";
//    @Value("${server.static-root}")
//    private String pathPatterns;
//
//    @Bean
//    public WebMvcConfigurer webMvcConfigurerAdapter() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
////                if (!registry.hasMappingForPattern(pathPatterns)) {
//                    registry.addResourceHandler(pathPatterns)
//                            .addResourceLocations("file:" + filePath);
////                }
//            }
//        };
//    }
}