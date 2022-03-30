package systembot.website.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import systembot.SystemBot;
import systembot.website.entity.Staff;
import systembot.website.repositories.StaffRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private final StaffRepository staffRepository;

    @Autowired
    public DatabaseLoader(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        if (false) {
            try {
                staffRepository.save(new Staff(SystemBot.api.getUserById(770240444466069514L).get(),
                        "Developer of the Plugin for the Mindustry Server and didn't make this Website.", "admin"));
                staffRepository.save(new Staff(SystemBot.api.getUserById(678060551888175145L).get(),
                        "Developer of the Expanded Industries Mod.", "admin"));
                staffRepository.save(new Staff(SystemBot.api.getUserById(687347431548911644L).get(),
                        "Administrator on the Phoenix Network Discord and Mindustry server.", "admin"));
                staffRepository.save(new Staff(SystemBot.api.getUserById(398683530588061697L).get(),
                        "He built the website better than Nautilus did when he built a Phoenix-Network.", "admin"));
                staffRepository.save(new Staff(SystemBot.api.getUserById(498158284512034816L).get(),
                        "Intensive gamer and friend of Nautilus. I don't know why he is an administrator.", "admin"));
                staffRepository.save(new Staff(SystemBot.api.getUserById(949658547023650936L).get(),
                        "Ex Administrator on the Phoenix Network Discord and Mindustry server.", "admin"));
            } catch (Exception e) {
                System.out.println("Could not load user");
                e.printStackTrace();
            }
        }
    }
}
