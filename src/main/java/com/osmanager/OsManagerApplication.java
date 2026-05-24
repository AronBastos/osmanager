package com.osmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OSManager — Service Order Management System
 * Spring Boot entry point.
 *
 * ============================================================
 * @SpringBootApplication — INTERVIEW NOTE (Topic 4)
 * ============================================================
 * This single annotation is actually a composite of THREE annotations:
 *
 * 1. @SpringBootConfiguration
 *    - Marks this as a Spring configuration class (like @Configuration).
 *    - Allows defining @Bean methods here if needed.
 *
 * 2. @EnableAutoConfiguration
 *    - THE core of Spring Boot's magic.
 *    - Scans the classpath and automatically creates beans for libraries
 *      it finds (e.g., sees HikariCP → creates DataSource bean automatically).
 *    - Uses @ConditionalOnClass, @ConditionalOnMissingBean — so your own
 *      beans always take precedence over auto-configured ones.
 *
 * 3. @ComponentScan
 *    - Scans the current package and all sub-packages for:
 *      @Component, @Service, @Repository, @Controller, @RestController,
 *      @Configuration — and registers them as Spring beans.
 *    - This is WHY package structure matters: put everything under
 *      com.osmanager so the scan finds everything automatically.
 *
 * Q: "What is the difference between @Configuration and @SpringBootApplication?"
 * A: @SpringBootApplication IS a @Configuration, but also enables component
 *    scanning and auto-configuration. Use @Configuration for secondary config
 *    classes (e.g., SecurityConfig, OpenApiConfig).
 * ============================================================
 */
@SpringBootApplication
public class OsManagerApplication {

    /**
     * Application entry point.
     *
     * SpringApplication.run() does:
     *   1. Creates the ApplicationContext (the IoC container)
     *   2. Triggers component scan
     *   3. Runs auto-configuration
     *   4. Starts the embedded Tomcat server
     *   5. Logs "Started OsManagerApplication in X seconds"
     */
    public static void main(String[] args) {
        SpringApplication.run(OsManagerApplication.class, args);
    }
}
