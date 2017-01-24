package net.mguenther.kafkasampler.tweetprocessing;

import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscription;
import twitter4j.Status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class FeedManager {

    private final ConcurrentHashMap<FeedId, Subscription> activeSubscriptions = new ConcurrentHashMap<>();

    /**
     * Establishes a new feed that filters an incoming Twitter tweet stream for the given keywords.
     *
     * @param keywords
     *      {@code List} of keywords that have to be present in a tweet
     * @return
     *      instance of {@code FeedId}, which provides a unique handle for that feed
     */
    public FeedId feed(final List<String> keywords) {
        final FeedId tweetFeed = new FeedId(keywords);
        final Observable<Status> observable = Observable
                .create(new FeedSubscriber(tweetFeed))
                .share()
                .sample(100, TimeUnit.MILLISECONDS);
        final Subscription subscription = observable.subscribe(
                nextStatus -> log.info(nextStatus.getText()),
                e -> log.error("Caught an exception: {}", e));
        activeSubscriptions.put(tweetFeed, subscription);
        return tweetFeed;
    }

    /**
     * @return
     *      unmodifiable {@code List} of feed handles for all feeds that are currently active
     */
    public List<FeedId> activeFeeds() {
        return Collections.unmodifiableList(activeSubscriptions.keySet().stream().collect(Collectors.toList()));
    }

    /**
     * Cancels the feed identified by {@code FeedId}.
     *
     * @param feed
     *      unique handle for a feed
     */
    public void cancel(final FeedId feed) {
        if (activeSubscriptions.containsKey(feed)) {
            log.info("Feed {} is not active.");
            return;
        }
        final Subscription subscription = activeSubscriptions.get(feed);
        subscription.unsubscribe();
        activeSubscriptions.remove(feed);
        log.info("Unsubscribed from {}.", feed);
    }

    public static void main(String[] args) throws Exception {

        log.info("Starting");

        final FeedManager feeder = new FeedManager();
        final FeedId tweetFeed = feeder.feed(Arrays.asList("trump"));

        TimeUnit.SECONDS.sleep(10);

        feeder.cancel(tweetFeed);

        log.info("Cancelled the feed.");

        TimeUnit.SECONDS.sleep(10);

        log.info("Nothing should be printed past this point.");
    }
}
