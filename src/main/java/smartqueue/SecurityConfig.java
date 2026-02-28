package smartqueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disabled to allow JavaScript fetch requests to work
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to see the website files and use the customer API
                .requestMatchers("/", "/index.html", "/bookAppointment.js", "/api/customer/**").permitAll()
                // Require a login for any employee dashboard actions
                .requestMatchers("/api/employee/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.permitAll()) // Provides the "Please sign in" page
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // This creates a temporary login
        UserDetails employee = User.withDefaultPasswordEncoder()
                .username("staff")
                .password("pass")
                .roles("EMPLOYEE")
                .build();

        return new InMemoryUserDetailsManager(employee);
    }
}