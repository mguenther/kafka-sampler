package net.mguenther.kafkasampler.tweetprocessing.sanitizing;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;

/**
 * The tweet duplication filter is based on a non-distributed bloom filter for simplicity.
 * This implementation constrains the de-duplication filter to a single running instance.
 * Running the sentiment analysis on multiple nodes would require coordination in the form
 * of a distributed bloom filter.
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class TweetDeduplicationFilter {

    private static final int DEFAULT_SIZE = 131_072;

    private final BloomFilter<Tweet> seenMessages;

    public TweetDeduplicationFilter() {
        this(DEFAULT_SIZE);
    }

    public TweetDeduplicationFilter(final int sizeOfBloomFilter) {
        this.seenMessages = BloomFilter.create(new TweetFunnel(), sizeOfBloomFilter);
    }

    public synchronized boolean seenBefore(final Tweet tweet) {
        if (seenMessages.mightContain(tweet)) {
            // we probably have seen that tweet before
            log.trace("Tweet with ID {} is received for the first time. This might be a false-positive.", tweet.getTweetId());
            return true;
        } else {
            // we definitely have not seen the tweet, so store it
            log.trace("Tweet with ID {} has not been received before for certain.", tweet.getTweetId());
            seenMessages.put(tweet);
            return false;
        }
    }

    public synchronized boolean notSeenBefore(final Tweet tweet) {
        return !seenBefore(tweet);
    }

    class TweetFunnel implements Funnel<Tweet> {

        @Override
        public void funnel(final Tweet from, final PrimitiveSink into) {
            into.putLong(from.getTweetId());
        }
    }
}
