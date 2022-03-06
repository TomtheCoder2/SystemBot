package website.controller;

import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import systembot.SystemBot;

import java.util.Map;

import static website.Administrators.*;

@Controller
public class HomeController {
    @RequestMapping(value = "/")
    public String index(Model model) {
        try {
            for (Map.Entry<String, User> admin : Admins.entrySet()) {
                model.addAttribute(admin.getKey() + "Url", admin.getValue().getAvatar().getUrl().toString());
            }
        } catch(Exception e) {
            System.out.println("There was an error while getting avatar images: ");
            e.printStackTrace();
        }
        return "index";
    }

    @RequestMapping(value = "/adminTest")
    public String adminTest() {
        return "AdminTest";
    }
}
