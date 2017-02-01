package net.mguenther.kafkasampler.tweetprocessing.feeder;

import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import net.mguenther.kafkasampler.tweetprocessing.domain.AnalyzedTweet;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class AnalyzedTweetCodec extends JsonCodec<AnalyzedTweet> {

    public AnalyzedTweetCodec() {
        super(AnalyzedTweet.class);
    }
}
