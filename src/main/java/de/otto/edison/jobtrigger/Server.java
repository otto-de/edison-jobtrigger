package de.otto.edison.jobtrigger;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
Extend the basePackages to de.otto.edison so SpringBoot is able to find
the components configured by edison-microservice
*/
@ComponentScan(basePackages = "de.otto.edison")
@SpringBootApplication
public class Server {

    private static ApplicationContext ctx;

    public static ApplicationContext applicationContext() {
        return ctx;
    }

    public static void main(String[] args) {
        ctx = new SpringApplication(Server.class).run(args);
    }
}
