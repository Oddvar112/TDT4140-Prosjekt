package appevent.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The main class for the Server Application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"appevent.core", "appevent.server", "appevent.dto", "appevent.model"})
@EntityScan("appevent.model")
@EnableJpaRepositories("appevent.model")
public class ServerApplication {
    /**
     * The main method which serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
