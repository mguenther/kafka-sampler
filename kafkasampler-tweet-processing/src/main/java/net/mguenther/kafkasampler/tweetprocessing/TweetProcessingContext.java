package net.mguenther.kafkasampler.tweetprocessing;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import net.mguenther.kafkasampler.adapter.elasticsearch.ClientFactory;
import net.mguenther.kafkasampler.adapter.elasticsearch.ElasticsearchSettings;
import net.mguenther.kafkasampler.adapter.elasticsearch.Feeder;
import net.mguenther.kafkasampler.adapter.kafka.ConsumerSettings;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.tweetprocessing.domain.AnalyzedTweet;
import net.mguenther.kafkasampler.tweetprocessing.enrichment.SentimentAnalyzer;
import net.mguenther.kafkasampler.tweetprocessing.feeder.AnalyzedTweetCodec;
import net.mguenther.kafkasampler.tweetprocessing.feeder.AnalyzedTweetConsumer;
import net.mguenther.kafkasampler.tweetprocessing.feeder.AnalyzedTweetDocument;
import net.mguenther.kafkasampler.tweetprocessing.feeder.AnalyzedTweetProcessor;
import net.mguenther.kafkasampler.tweetprocessing.ingest.IngestManager;
import net.mguenther.kafkasampler.tweetprocessing.ingest.RawTweetCodec;
import net.mguenther.kafkasampler.tweetprocessing.ingest.RawTweetProducer;
import net.mguenther.kafkasampler.tweetprocessing.ingest.StatusToTweetConverter;
import net.mguenther.kafkasampler.tweetprocessing.sanitizing.TweetDeduplicationFilter;
import net.mguenther.kafkasampler.tweetprocessing.sanitizing.TweetSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import twitter4j.Status;

import java.util.Arrays;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Configuration
public class TweetProcessingContext {

    @Bean
    public IngestManager feedManager(@Autowired RawTweetProducer producer,
                                     @Autowired StatusToTweetConverter converter,
                                     @Autowired TweetProcessingConfig config) {
        final IngestManager manager = new IngestManager(producer, converter, config.getTopicForRawTweets());
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
    public StatusToTweetConverter statusToTweetConverter() {
        return new StatusToTweetConverter();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner schedulingRunner(@Autowired TaskExecutor executor,
                                              @Autowired AnalyzedTweetConsumer consumer) {
        return args -> executor.execute(consumer);
    }

    @Bean
    public AnalyzedTweetConsumer analyzedTweetConsumer(@Autowired AnalyzedTweetCodec codec,
                                                       @Autowired AnalyzedTweetProcessor processor,
                                                       @Autowired TweetProcessingConfig config) {
        final ConsumerSettings<String, AnalyzedTweet> settings  = ConsumerSettings
                .builder(config.getFeederId(), codec, processor)
                .usingBootstrapServer(config.getBrokerUrl())
                .build();
        return new AnalyzedTweetConsumer(config.getFeederId(), config.getTopicForAnalyzedTweets(), settings);
    }

    @Bean
    public AnalyzedTweetCodec analyzedTweetCodec() {
        return new AnalyzedTweetCodec();
    }

    @Bean
    public AnalyzedTweetProcessor analyzedTweetProcessor(@Autowired Feeder<AnalyzedTweetDocument> feeder) {
        return new AnalyzedTweetProcessor(feeder);
    }

    @Bean
    public Feeder<AnalyzedTweetDocument> feeder(@Autowired TweetProcessingConfig config) {
        final ElasticsearchSettings settings = ElasticsearchSettings.usingDefaults(config.getElasticsearchHost(), config.getElasticsearchPort());
        return new Feeder<>(new ClientFactory(settings), config.getIndex());
    }

    @Bean
    public StanfordCoreNLP pipeline() {
        return new StanfordCoreNLP("nlpcore.properties");
    }

    @Bean
    public SentimentAnalyzer analyzer(final StanfordCoreNLP pipeline) {
        return new SentimentAnalyzer(pipeline);
    }

    @Bean
    public TweetDeduplicationFilter filter() {
        return new TweetDeduplicationFilter();
    }

    @Bean
    public TweetSanitizer sanitizer() {
        return new TweetSanitizer();
    }

    @Bean
    public TwitterSentimentAnalysis processing(@Autowired TweetDeduplicationFilter filter,
                                               @Autowired TweetSanitizer sanitizer,
                                               @Autowired SentimentAnalyzer analyzer,
                                               @Autowired TweetProcessingConfig config) {
        return new TwitterSentimentAnalysis(filter, sanitizer, analyzer, config);
    }
}
