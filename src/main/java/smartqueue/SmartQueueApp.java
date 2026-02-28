package smartqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartQueueApp {

    public static void main(String[] args) {
        // This launches the embedded Tomcat web server and initializes the Spring context
        SpringApplication.run(SmartQueueApp.class, args);
    }
}