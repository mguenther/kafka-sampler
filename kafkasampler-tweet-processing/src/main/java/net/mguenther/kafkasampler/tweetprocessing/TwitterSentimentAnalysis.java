package net.mguenther.kafkasampler.tweetprocessing;

import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.StreamSettings;
import net.mguenther.kafkasampler.tweetprocessing.domain.AnalyzedTweet;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;
import net.mguenther.kafkasampler.tweetprocessing.enrichment.SentimentAnalyzer;
import net.mguenther.kafkasampler.tweetprocessing.sanitizing.StatusDeduplicationFilter;
import net.mguenther.kafkasampler.tweetprocessing.sanitizing.TweetSanitizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
@Component
public class TwitterSentimentAnalysis {

    private final StatusDeduplicationFilter filter;

    private final TweetSanitizer sanitizer;

    private final SentimentAnalyzer analyzer;

    private final TweetProcessingConfig config;

    private final KafkaStreams streams;

    public TwitterSentimentAnalysis(final StatusDeduplicationFilter filter,
                                    final TweetSanitizer sanitizer,
                                    final SentimentAnalyzer analyzer,
                                    final TweetProcessingConfig config) {
        this.filter = filter;
        this.sanitizer = sanitizer;
        this.analyzer = analyzer;
        this.config = config;
        this.streams = setupKafkaStreams();
    }

    @PostConstruct
    public void initializingCompleted() {
        start();
    }

    @PreDestroy
    public void applicationStopped() {
        stop();
    }

    public void start() {
        streams.start();
        log.info("Started stream processing.");
    }

    public void stop() {
        streams.close();
        streams.cleanUp();
        log.info("Closed all stream processors of this streaming application.");
    }

    private KafkaStreams setupKafkaStreams() {
        log.info("Constructing stream processor topology.");
        final StreamSettings<String, String> settings = StreamSettings.usingDefaults(config.getApplicationId());
        log.info("Using the following Kafka Streams config: {}", settings);
        final KStreamBuilder builder = new KStreamBuilder();
        setupSanitizingStream(builder);
        setupAnalyticsStream(builder);
        return new KafkaStreams(builder, settings.getProperties());
    }

    private void setupSanitizingStream(final KStreamBuilder builder) {
        final KStream<String, String> fromRaw = builder.stream(config.getTopicForRawTweets());
        final KStream<String, String> toSanitize = fromRaw
                .mapValues(this::toStatus)
                .filter((key, value) -> filter.notSeenBefore(value))
                .mapValues(sanitizer::convert)
                .mapValues(Tweet::toJson);
        toSanitize.to(Serdes.String(), Serdes.String(), config.getTopicForSanitizedTweets());
        log.info("Tweet Sanitizing stream is configured.");
    }

    private void setupAnalyticsStream(final KStreamBuilder builder) {
        final KStream<String, String> fromSanitize = builder.stream(config.getTopicForSanitizedTweets());
        final KStream<String, String> toAnalyze = fromSanitize
                .mapValues(Tweet::fromJson)
                .filter((key, value) -> StringUtils.isNotEmpty(value.getText()))
                .mapValues(analyzer::analyze)
                .mapValues(AnalyzedTweet::toJson);
        toAnalyze.to(Serdes.String(), Serdes.String(), config.getTopicForAnalyzedTweets());
        log.info("Tweet Analyzation stream is configured.");
    }

    private Status toStatus(String json) {
        try {
            return TwitterObjectFactory.createStatus(json);
        } catch (TwitterException e) {
            log.warn("Could not deserialize JSON string to instance of Status.");
            return null;
        }
    }
}
