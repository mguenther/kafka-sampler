package net.mguenther.kafkasampler.tweetprocessing.ingest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import rx.Observable;
import rx.Subscription;
import twitter4j.Status;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class IngestManager {

    private final ConcurrentHashMap<IngestHandle, Subscription> activeSubscriptions = new ConcurrentHashMap<>();

    private final RawTweetProducer producer;

    private final String topic;

    public IngestManager(@Autowired final RawTweetProducer producer,
                         @Value("${ingestSettings.topic}") final String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    /**
     * Establishes a new feed that filters an incoming Twitter tweet stream for the given keywords.
     *
     * @param keywords
     *      {@code List} of keywords that have to be present in a tweet
     * @return
     *      instance of {@code IngestHandle}, which provides a unique handle for that feed
     */
    public IngestHandle feed(final List<String> keywords) {
        final IngestHandle tweetFeed = new IngestHandle(keywords);
        final Observable<Status> observable = Observable
                .create(new TwitterStreamObservable(tweetFeed))
                .share()
                .sample(3, TimeUnit.SECONDS);
        final Subscription subscription = observable.subscribe(new TwitterStreamSubscriber(producer, topic));
        activeSubscriptions.put(tweetFeed, subscription);
        return tweetFeed;
    }

    /**
     * @return
     *      unmodifiable {@code List} of feed handles for all feeds that are currently active
     */
    public List<IngestHandle> activeFeeds() {
        return Collections.unmodifiableList(activeSubscriptions.keySet().stream().collect(Collectors.toList()));
    }

    /**
     * Cancels the feed identified by {@code IngestHandle}.
     *
     * @param feed
     *      unique handle for a feed
     */
    public void cancel(final IngestHandle feed) {
        if (activeSubscriptions.containsKey(feed)) {
            log.info("Feed {} is not active.");
            return;
        }
        final Subscription subscription = activeSubscriptions.get(feed);
        subscription.unsubscribe();
        activeSubscriptions.remove(feed);
        log.info("Unsubscribed from {}.", feed);
    }
}
