package systembot.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import systembot.website.repositories.StaffRepository;

@Controller
@RequestMapping("/staff")
public class StaffIndexController {
    public @Autowired StaffRepository staffRepository;

    @Value("${spring.thymeleaf.prefix}")
    private String webroot;

    @RequestMapping
    public String getVideo() {
        return "staff";
    }
}
