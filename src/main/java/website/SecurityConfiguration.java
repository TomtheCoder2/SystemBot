package website;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Greg Turnquist
 */
// tag::code[]
@Configuration
@EnableWebSecurity // <1>
@EnableGlobalMethodSecurity(prePostEnabled = true) // <2>
public class SecurityConfiguration extends WebSecurityConfigurerAdapter { // <3>

    @Override
    protected void configure(HttpSecurity http) throws Exception { // <5>
        http
                .authorizeRequests()
                .antMatchers("/built/**", "/static/**", "/static/css/**", "/", "/home").permitAll();
    }

    @Bean @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}
