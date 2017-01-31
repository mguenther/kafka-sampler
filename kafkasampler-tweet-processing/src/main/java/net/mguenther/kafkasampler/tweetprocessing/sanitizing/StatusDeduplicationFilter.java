package net.mguenther.kafkasampler.tweetprocessing.sanitizing;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class StatusDeduplicationFilter {

    private static final int DEFAULT_SIZE = 131_072;

    private final BloomFilter<Status> seenMessages;

    public StatusDeduplicationFilter() {
        this(DEFAULT_SIZE);
    }

    public StatusDeduplicationFilter(final int sizeOfBloomFilter) {
        this.seenMessages = BloomFilter.create(new StatusFunnel(), sizeOfBloomFilter);
    }

    public synchronized boolean seenBefore(final Status status) {
        if (seenMessages.mightContain(status)) {
            // we probably have seen that tweet before
            log.trace("Tweet with ID {} is received for the first time. This might be a false-positive.", status.getId());
            return true;
        } else {
            // we definitely have not seen the tweet, so store it
            log.trace("Tweet with ID {} has not been received before for certain.", status.getId());
            seenMessages.put(status);
            return false;
        }
    }

    public synchronized boolean notSeenBefore(final Status status) {
        return !seenBefore(status);
    }

    class StatusFunnel implements Funnel<Status> {

        @Override
        public void funnel(final Status from, final PrimitiveSink into) {
            into.putLong(from.getId());
        }
    }
}
