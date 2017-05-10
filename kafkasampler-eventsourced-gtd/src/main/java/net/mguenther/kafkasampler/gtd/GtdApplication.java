package net.mguenther.kafkasampler.gtd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@SpringBootApplication
public class GtdApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(GtdApplication.class, args);
        } catch (Exception e) {
            System.err.println("Unable to start the GTD application.");
            System.err.println("Aborting.");
            System.exit(1);
        }
    }
}
