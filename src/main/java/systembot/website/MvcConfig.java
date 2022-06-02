package systembot.website;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("gallery").setViewName("gallery");
        // team.html
        registry.addViewController("/team").setViewName("team");
        // changelist
        registry.addViewController("/changelist").setViewName("changelist");
    }
}
