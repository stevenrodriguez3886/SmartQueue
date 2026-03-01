package smartqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @brief Main execution class for the SmartQueue Spring Boot application.
 */
@SpringBootApplication
public class SmartQueueApp {

    /**
     * @brief Main method to launch the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // This launches the embedded Tomcat web server and initializes the Spring application context
        SpringApplication.run(SmartQueueApp.class, args);
    }
}