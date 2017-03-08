package net.mguenther.kafkasampler.tweetprocessing.ingest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import rx.Observable;
import rx.Subscription;
import twitter4j.Status;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class IngestManager {

    private final ConcurrentHashMap<String, Ingest> activeSubscriptions = new ConcurrentHashMap<>();

    private final RawTweetProducer producer;

    private final StatusToTweetConverter converter;

    private final String topic;

    public IngestManager(@Autowired final RawTweetProducer producer,
                         @Autowired final StatusToTweetConverter converter,
                         @Value("${ingestSettings.topic}") final String topic) {
        this.producer = producer;
        this.converter = converter;
        this.topic = topic;
    }

    /**
     * Establishes a new feed that filters an incoming Twitter tweet stream for the given keywords.
     *
     * @param keywords
     *      {@code List} of keywords that have to be present in a tweet
     * @return
     *      instance of {@code Ingest}, which provides a unique handle for that feed
     */
    public Ingest feed(final List<String> keywords) {
        final String ingestId = UUID.randomUUID().toString().substring(0, 7);
        final Observable<Status> observable = Observable
                .create(new TwitterStreamObservable(ingestId, keywords))
                .share()
                .sample(100, TimeUnit.MILLISECONDS);
        final Subscription subscription = observable.subscribe(new TwitterStreamSubscriber(producer, converter, topic));
        final Ingest tweetFeed = new Ingest(ingestId, keywords, subscription);
        activeSubscriptions.put(ingestId, tweetFeed);
        return tweetFeed;
    }

    /**
     * @return
     *      unmodifiable {@code List} of feed handles for all feeds that are currently active
     */
    public List<Ingest> activeIngests() {
        return Collections.unmodifiableList(activeSubscriptions.values().stream().collect(Collectors.toList()));
    }

    /**
     * Cancels the feed identified by {@code Ingest}.
     *
     * @param ingestId
     *      ID that identifies the tweet handle
     */
    public void cancel(final String ingestId) {
        if (!activeSubscriptions.containsKey(ingestId)) {
            log.info("Feed {} is not active.");
            return;
        }
        final Subscription subscription = activeSubscriptions.get(ingestId).getSubscription();
        subscription.unsubscribe();
        activeSubscriptions.remove(ingestId);
        log.info("Unsubscribed from {}.", ingestId);
    }
}
