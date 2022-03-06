package website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import systembot.SystemBot;

@EnableJpaRepositories("website.repositories")
@EntityScan("website.entity")
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SystemBot.main(new String[]{});
        SpringApplication.run(Main.class, args);
    }
}
