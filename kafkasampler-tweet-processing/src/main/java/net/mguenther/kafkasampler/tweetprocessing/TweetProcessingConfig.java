package net.mguenther.kafkasampler.tweetprocessing;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Configuration
@Getter
public class TweetProcessingConfig {

    @Value("${ingestion.topic}")
    private String topicForRawTweets;

    @Value("${ingestion.keywords}")
    private String keywords;

    @Value("${sanitizing.topic}")
    private String topicForSanitizedTweets;

    @Value("${analyzing.topic}")
    private String topicForAnalyzedTweets;

    @Value("${streaming.applicationId}")
    private String applicationId;

    @Value("${streaming.zookeeperUrl}")
    private String zookeeperUrl;

    @Value("${streaming.brokerUrl}")
    private String brokerUrl;
}
