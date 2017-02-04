package net.mguenther.kafkasampler.tweetprocessing.ingest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafkasampler.adapter.kafka.Producer;
import net.mguenther.kafkasampler.tweetprocessing.domain.Tweet;
import rx.Subscriber;
import twitter4j.Status;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Slf4j
@RequiredArgsConstructor
public class TwitterStreamSubscriber extends Subscriber<Status> {

    private final Producer<Tweet, String> rawTweetProducer;
    private final StatusToTweetConverter converter;
    private final String rawTweetTopic;

    @Override
    public void onCompleted() {
        log.info("Received completed signal.");
    }

    @Override
    public void onError(final Throwable throwable) {
        // already caught and logged in TwitterStreamObservable
        // we will not do any kind of compensation logic at this point
    }

    @Override
    public void onNext(final Status status) {

        try {
            rawTweetProducer.log(rawTweetTopic, converter.convert(status));
            log.debug("Committed raw tweet with ID {} to Kafka log {}.", status.getId(), rawTweetTopic);
        } catch (Exception e) {
            log.warn("Unable to log status {} to Kafka.", status, e);
        }
    }
}
