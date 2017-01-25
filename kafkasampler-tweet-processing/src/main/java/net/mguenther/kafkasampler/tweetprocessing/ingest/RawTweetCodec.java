package net.mguenther.kafkasampler.tweetprocessing.ingest;

import net.mguenther.kafkasampler.adapter.kafka.JsonCodec;
import twitter4j.Status;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class RawTweetCodec extends JsonCodec<Status> {

    public RawTweetCodec() {
        super(Status.class);
    }
}
