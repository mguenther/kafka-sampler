package net.mguenther.kafkasampler.tweetprocessing;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.tweetprocessing.ingest.IngestManager;
import net.mguenther.kafkasampler.tweetprocessing.ingest.RawTweetCodec;
import net.mguenther.kafkasampler.tweetprocessing.ingest.RawTweetProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
                                     @Value("${ingestSettings.topic:kafkasampler-raw-tweets}") String topic,
                                     @Value("${ingestSettings.keywords}") String keywords) {
        final IngestManager manager = new IngestManager(producer, topic);
        manager.feed(Arrays.asList(keywords));
        return manager;
    }

    @Bean
    public RawTweetProducer rawTweetProducer(@Autowired RawTweetCodec codec,
                                             @Value("${kafkaSettings.brokerUrl:localhost:9092}") String brokerUrl) {
        final ProducerSettings<Status, String> settings = ProducerSettings
                .builder(codec)
                .usingBootstrapServer(brokerUrl)
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
}
