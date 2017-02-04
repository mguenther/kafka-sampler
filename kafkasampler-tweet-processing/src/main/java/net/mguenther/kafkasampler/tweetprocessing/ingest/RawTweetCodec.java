package net.mguenther.kafkasampler.tweetprocessing.ingest;

import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class RawTweetCodec extends JsonCodec<Tweet> {

    public RawTweetCodec() {
        super(Tweet.class);
    }
}
