package net.mguenther.kafkasampler.tweetprocessing.ingest;

import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.adapter.kafka.ProducerSettings;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class RawTweetProducer extends Producer<Tweet, String> {

    public RawTweetProducer(final String producerId,
                            final ProducerSettings<Tweet, String> producerSettings) {
        super(producerId, producerSettings);
    }
}
