package net.mguenther.kafkasampler.tweetprocessing;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.tweetprocessing.enrichment.CoreNlpSentimentAnalyzer;
import net.mguenther.kafkasampler.tweetprocessing.enrichment.SentimentAnalyzer;
import net.mguenther.kafkasampler.tweetprocessing.ingest.IngestManager;
import net.mguenther.kafkasampler.tweetprocessing.ingest.RawTweetCodec;
import net.mguenther.kafkasampler.tweetprocessing.ingest.RawTweetProducer;
import net.mguenther.kafkasampler.tweetprocessing.sanitizing.StatusDeduplicationFilter;
import net.mguenther.kafkasampler.tweetprocessing.sanitizing.TweetSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Status;

import java.util.Arrays;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Configuration
public class TweetProcessingContext {

    @Bean
    public IngestManager feedManager(@Autowired RawTweetProducer producer,
                                     @Autowired TweetProcessingConfig config) {
        final IngestManager manager = new IngestManager(producer, config.getTopicForRawTweets());
        manager.feed(Arrays.asList(config.getKeywords()));
        return manager;
    }

    @Bean
    public RawTweetProducer rawTweetProducer(@Autowired RawTweetCodec codec,
                                             @Autowired TweetProcessingConfig config) {
        final ProducerSettings<Status, String> settings = ProducerSettings
                .builder(codec)
                .usingBootstrapServer(config.getBrokerUrl())
                .build();
        return new RawTweetProducer("producer-raw-tweet-1", ProducerSettings.usingDefaults(codec));
    }

    @Bean
    public RawTweetCodec rawTweetCodec() {
        return new RawTweetCodec();
    }

    @Bean
    public StanfordCoreNLP pipeline() {
        return new StanfordCoreNLP("nlpcore.properties");
    }

    @Bean
    public SentimentAnalyzer analyzer(final StanfordCoreNLP pipeline) {
        return new CoreNlpSentimentAnalyzer(pipeline);
    }

    @Bean
    public StatusDeduplicationFilter filter() {
        return new StatusDeduplicationFilter();
    }

    @Bean
    public TweetSanitizer sanitizer() {
        return new TweetSanitizer();
    }

    @Bean
    public TwitterSentimentAnalysis processing(@Autowired StatusDeduplicationFilter filter,
                                               @Autowired TweetSanitizer sanitizer,
                                               @Autowired SentimentAnalyzer analyzer,
                                               @Autowired TweetProcessingConfig config) {
        return new TwitterSentimentAnalysis(filter, sanitizer, analyzer, config);
    }
}
