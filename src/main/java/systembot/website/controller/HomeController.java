package systembot.website.controller;

import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import systembot.SystemBot;

import java.util.Map;

@Controller
public class HomeController {
    @RequestMapping(value = "/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping(value = "/adminTest")
    public String adminTest() {
        return "AdminTest";
    }
}
