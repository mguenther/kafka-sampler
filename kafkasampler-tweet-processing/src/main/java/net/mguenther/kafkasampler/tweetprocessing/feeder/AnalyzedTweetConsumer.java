package net.mguenther.kafkasampler.tweetprocessing.feeder;

import net.mguenther.kafkasampler.adapter.kafka.Consumer;
import net.mguenther.kafkasampler.adapter.kafka.ConsumerSettings;
import net.mguenther.kafkasampler.tweetprocessing.domain.AnalyzedTweet;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class AnalyzedTweetConsumer extends Consumer<String, AnalyzedTweet> {

    public AnalyzedTweetConsumer(final String consumerId,
                                 final String topic,
                                 final ConsumerSettings<String, AnalyzedTweet> consumerSettings) {
        super(consumerId, topic, consumerSettings);
    }
}
