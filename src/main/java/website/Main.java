package website;

import com.github.valb3r.letsencrypthelper.tomcat.TomcatWellKnownLetsEncryptChallengeEndpointConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import systembot.SystemBot;

@EnableJpaRepositories("website.repositories")
@EntityScan("website.entity")
@SpringBootApplication
@Import(TomcatWellKnownLetsEncryptChallengeEndpointConfig.class) // Enable LetsEncrypt certificate management
public class Main {
    public static void main(String[] args) {
        // https://stackoverflow.com/questions/39632667/how-do-i-kill-the-process-currently-using-a-port-on-localhost-in-windows
        // netstat -ano | findstr :80
        // taskkill /PID <PID> /F
//        SystemBot.main(new String[]{});
        SpringApplication.run(Main.class, args);
        SystemBot.main(new String[]{});
    }
}
