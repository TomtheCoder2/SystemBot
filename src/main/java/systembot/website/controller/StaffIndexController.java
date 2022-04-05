package systembot.website.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import systembot.website.repositories.StaffRepository;

import java.net.MalformedURLException;

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
