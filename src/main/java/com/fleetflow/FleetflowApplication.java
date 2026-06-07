package com.fleetflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * FleetFlowApplication — The main entry point of the entire application.
 *
 * When you click "Run" in IntelliJ or run "mvn spring-boot:run",
 * Java starts HERE. This class boots up the entire Spring framework,
 * connects to the database, runs Flyway migrations, and starts
 * listening for HTTP requests on port 8080.
 *
 * Annotations explained:
 *
 * @SpringBootApplication — combines three annotations in one:
 *   @Configuration     = this class can define Spring beans
 *   @EnableAutoConfiguration = Spring auto-configures based on dependencies
 *   @ComponentScan     = Spring scans this package for @Service, @Controller etc.
 *
 * @EnableJpaAuditing — enables @CreationTimestamp and @UpdateTimestamp
 *   on our entity fields. Without this, those timestamps won't auto-fill.
 */
@SpringBootApplication
@EnableJpaAuditing
public class FleetflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetflowApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════════════╗
                ║   FleetFlow is running!                      ║
                ║                                              ║
                ║   API:     http://localhost:8080             ║
                ║   Swagger: http://localhost:8080/swagger-ui.html ║
                ╚══════════════════════════════════════════════╝
                """);
    }
}
