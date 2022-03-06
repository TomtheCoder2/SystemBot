package website.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import systembot.SystemBot;
import website.entity.Admin;
import website.repositories.AdminRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private final AdminRepository adminRepository;

    @Autowired
    public DatabaseLoader(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        if (false) {
            try {
                adminRepository.save(new Admin(SystemBot.api.getUserById(770240444466069514L).get(),
                        "Developer of the Plugin for the Mindustry Server and didn't make this Website."));
                adminRepository.save(new Admin(SystemBot.api.getUserById(678060551888175145L).get(),
                        "Developer of the Expanded Industries Mod."));
                adminRepository.save(new Admin(SystemBot.api.getUserById(687347431548911644L).get(),
                        "Administrator on the Phoenix Network Discord and Mindustry server."));
                adminRepository.save(new Admin(SystemBot.api.getUserById(398683530588061697L).get(),
                        "He built the website better than Nautilus did when he built a Phoenix-Network."));
                adminRepository.save(new Admin(SystemBot.api.getUserById(498158284512034816L).get(),
                        "Intensive gamer and friend of Nautilus. I don't know why he is an administrator."));
                adminRepository.save(new Admin(SystemBot.api.getUserById(949658547023650936L).get(),
                        "Ex Administrator on the Phoenix Network Discord and Mindustry server."));
            } catch (Exception e) {
                System.out.println("Could not load user");
                e.printStackTrace();
            }
        }
    }
}
