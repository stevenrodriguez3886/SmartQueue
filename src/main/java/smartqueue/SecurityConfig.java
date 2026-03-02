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

/**
 * @brief Configuration class for Spring Security.
 * * Defines endpoint authorization rules and creates an in-memory employee user.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * @brief Configures the HTTP security filter chain.
     * @param http The HttpSecurity builder.
     * @return The built SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disabled to allow standard JavaScript fetch POST/DELETE requests to work
            .authorizeHttpRequests(auth -> auth
                // Allow public access to the frontend, customer APIs, and WebSockets
                .requestMatchers("/", "/index.html", "/bookAppointment.js", "/api/customer/**", "/ws/**").permitAll() 
                // Restrict employee APIs to users with the "EMPLOYEE" role
                .requestMatchers("/api/employee/**").hasRole("EMPLOYEE")
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.permitAll()) // Enables default Spring Boot "Please sign in" page
            .logout(logout -> logout.permitAll()); // Enables logout functionality

        return http.build();
    }

    /**
     * @brief Configures an in-memory user store for authentication.
     * @return A UserDetailsService containing the hardcoded employee credentials.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // This creates a temporary login for the staff dashboard
        UserDetails employee = User.withDefaultPasswordEncoder()
                .username("staff")
                .password("pass")
                .roles("EMPLOYEE") // Grants the role required by the security chain above
                .build();

        return new InMemoryUserDetailsManager(employee);
    }
}