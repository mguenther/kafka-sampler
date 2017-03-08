package net.mguenther.kafkasampler.tweetprocessing.ingest;

import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscriber;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@code TwitterStreamObservable} starts off an underlying {@link TwitterStream} which is filtered based
 * on a set of keywords. It also handles shutdown management in case the subscription is revoked.
 * This code is heavily inspired by shekhargulati/rx-tweet-stream (GitHub).
 *
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
public class TwitterStreamObservable extends StatusAdapter implements Observable.OnSubscribe<Status> {

    private final String ingestId;

    private final FilterQuery filterBy;

    private final TwitterStream stream;

    private AtomicReference<Subscriber<? super Status>> subscriberRef = new AtomicReference<>(null);

    public TwitterStreamObservable(final String ingestId, final List<String> keywords) {
        this.ingestId = ingestId;
        this.filterBy = new FilterQuery();
        this.filterBy.track(keywords.toArray(new String[0]));
        log.info("FilterBy: {}", filterBy);
        this.stream = new TwitterStreamFactory().getInstance();
    }

    @Override
    public void call(final Subscriber<? super Status> subscriber) {
        if (!subscriberRef.compareAndSet(null, subscriber)) {
            log.warn("Unable to set the subscriber reference because there is already a subscriber set.");
            return;
        }
        stream.addListener(this);
        stream.filter(filterBy);
    }

    @Override
    public void onStatus(final Status status) {
        final Subscriber<? super Status> subscriber = subscriberRef.get();
        if (subscriber.isUnsubscribed()) {
            shutdownStream();
        } else {
            subscriber.onNext(status);
        }
    }

    @Override
    public void onException(final Exception e) {
        final Subscriber<? super Status> subscriber = subscriberRef.get();
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            log.error("Caught an exception from the Twitter4J Streaming API", e);
            subscriber.onError(e);
        }
        shutdownStream();
    }

    private void shutdownStream() {
        log.info("Shutting down stream for {}.", ingestId);
        if (stream != null) {
            stream.shutdown();
        }
        final Subscriber<? super Status> subscriber = subscriberRef.get();
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
            subscriber.unsubscribe();
        }
    }
}
