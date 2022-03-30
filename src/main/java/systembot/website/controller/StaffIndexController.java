package systembot.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import systembot.website.repositories.StaffRepository;

@Controller
@RequestMapping("/staff")
public class StaffIndexController {
    public @Autowired StaffRepository staffRepository;

    @RequestMapping
    public String index(Model model) {
        return "staffIndex";
    }
}
