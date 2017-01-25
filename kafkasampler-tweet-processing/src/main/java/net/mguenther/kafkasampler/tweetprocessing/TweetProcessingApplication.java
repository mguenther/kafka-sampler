package net.mguenther.kafkasampler.tweetprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@SpringBootApplication
public class TweetProcessingApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(TweetProcessingApplication.class, args);
        } catch (Exception e) {
            System.err.println("Unable to start the tweet processing engine.");
            System.err.println("Aborting.");
            System.exit(1);
        }
    }
}
